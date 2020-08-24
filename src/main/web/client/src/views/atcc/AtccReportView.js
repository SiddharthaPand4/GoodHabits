import React, {Component} from "react";
import {Button, Card, DatePicker, Dropdown, Icon, Menu, Modal, Select, Row, Col, Form, Spin, message} from "antd";
import DashboardService from "../../services/DashboardService";
import AtccService from "../../services/AtccService";
import moment from 'moment';
import {saveAs} from 'file-saver';
import FeedService from "../../services/FeedService";

const {Option} = Select;
const {RangePicker} = DatePicker;

export default class AtccReportView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            feeds: [],
            isOpenCustomDateRangeModal: "",
            report: {
                filter: {
                    selectedCustomDateRange: "Today",
                    feedId: 0,
                    reportType: "All Incidents",
                    reportFileType: "csv",
                    fromDate: moment().startOf('day').toDate(),
                    toDate: moment().endOf('day').toDate(),
                }
            },
            downloading: false,
        };

        this.selectDateRange = this.selectDateRange.bind(this);
        this.selectReportType = this.selectReportType.bind(this);

        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);
        this.getReportTypeOptions = this.getReportTypeOptions.bind(this);
        this.downloadReport = this.downloadReport.bind(this);
        this.handleChangeReportType = this.handleChangeReportType.bind(this);
    }

    componentDidMount() {
        this.fetchFeedsList();
    }

    fetchFeedsList = async () => {
        try {
            const res = await FeedService.getFeeds()
            const feeds = res.data
            this.setState({ feeds })
        } catch(e) {
            console.log(e)
            message.error("Something Went Wrong ")
        }
    }

    handleChangeReportType(value) {
        let report = {...this.state.report};
        let filter = report.filter;
        filter.reportFileType = value;
        this.setState({report: report})
    }

    showCustomDateRangeModal = ()=> {
        this.setState({
            isOpenCustomDateRangeModal: true,
        });
    };

    handleCancel = e => {
        this.setState({
            isOpenCustomDateRangeModal: false,
        });
    };

    selectDateRange(reportName, selectedCustomDateRangeEnum, selectedCustomDateRangeMoment) {
        let {isOpenCustomDateRangeModal} = this.state;
        let report = this.state.report;
        report.filter.selectedCustomDateRange = selectedCustomDateRangeEnum;
        let fromToDate = DashboardService.extractFromToDate(report.filter.selectedCustomDateRange, selectedCustomDateRangeMoment);
        report.filter.fromDate = fromToDate.from_date;
        report.filter.toDate = fromToDate.to_date;


        if (selectedCustomDateRangeEnum === "Custom") {
            isOpenCustomDateRangeModal = ""
        }
        this.setState({report, isOpenCustomDateRangeModal});

    }

    downloadReport() {
        this.setState({downloading: true});
        let filter = this.state.report.filter;
        let req = {
            fromDateString: moment(filter.fromDate).format('YYYY-MM-DD HH:mm:ss'),
            toDateString: moment(filter.toDate).format('YYYY-MM-DD HH:mm:ss"'),
            reportType: filter.reportType,
            reportFileType: filter.reportFileType,
            feedId: filter.feedId,
        };

        AtccService.getAtccReport(req).then(response => {
            this.setState({downloading: false});
            let fileName = filter.reportType + '.' + filter.reportFileType;
            saveAs(response.data, fileName);
        }).catch(error => {
            this.setState({downloading: false});
            alert(error);
            console.log(error);
        });
    }

    feedSelected = value => {
        const report = {...this.state.report}
        report.filter.feedId = value
        this.setState({report})
    }

    selectReportType(reportType) {
        let {report} = this.state;
        report.filter.reportType = reportType;
        this.setState({report});
    }

    getReportTypeOptions() {
        return (<Menu>

            <Menu.Item key="1"
                       onClick={() => this.selectReportType("All Incidents")}>
                All Incidents
            </Menu.Item>
            <Menu.Item key="2"
                       onClick={() => this.selectReportType("DayWise Incidents Summary")}>
                DayWise Incidents Summary
            </Menu.Item>
            <Menu.Item key="3"
                       onClick={() => this.selectReportType("All Vehicles Traffic Events")}>
                All Vehicles Traffic Events
            </Menu.Item>
            <Menu.Item key="4"
                       onClick={() => this.selectReportType("Vehicles Traffic Events Summary")}>
                Vehicles Traffic Events Summary
            </Menu.Item>
        </Menu>)
    }

    getDateRangeOptions() {
        return (
            <Menu>
                <Menu.Item key="1" onClick={() => this.selectDateRange("Today")}>
                    Today
                </Menu.Item>
                <Menu.Item key="2" onClick={() => this.selectDateRange("Yesterday")}>
                    Yesterday
                </Menu.Item>
                <Menu.Item key="3" onClick={() => this.selectDateRange("This week")}>
                    This week
                </Menu.Item>
                <Menu.Item key="4" onClick={() => this.selectDateRange("Last week")}>
                    Last week
                </Menu.Item>
                <Menu.Item key="5" onClick={() => this.selectDateRange("This month")}>
                    This month
                </Menu.Item>
                <Menu.Item key="6" onClick={() => this.selectDateRange("This year")}>
                    This year
                </Menu.Item>
                <Menu.Item key="7" onClick={() => this.selectDateRange("Last year")}>
                    Last year
                </Menu.Item>
                <Menu.Item onClick={this.showCustomDateRangeModal}>
                    Custom
                </Menu.Item>
            </Menu>
        );
    }

    render() {
        let {report} = this.state;
        return (
            <div>
                <br/>
                <br/>
                <div>
                    <Modal
                        onCancel={this.handleCancel}
                        title="Custom Date Range"
                        visible={this.state.isOpenCustomDateRangeModal}
                        footer={[]}
                    >
                        <RangePicker
                            onChange={(changedDateRange) => this.selectDateRange(this.state.isOpenCustomDateRangeModal, "Custom", changedDateRange)}/>
                    </Modal>
                </div>
                <Row>
                    <Col xl={{span: 8}} lg={{span: 6}} md={{span: 4}} sm={{span: 2}} xs={{span: 2}}/>
                    <Col xl={{span: 8}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>
                        <Card title={<div>Reports</div>}>
                            <Form>
                                <Form.Item>
                                    Select Date Range
                                    <Dropdown overlay={() => this.getDateRangeOptions("report")}>
                                        <Button style={{width: '-webkit-fill-available'}}>
                                            {report.filter.selectedCustomDateRange ? report.filter.selectedCustomDateRange : "Select"}
                                            <Icon
                                                type="down"/>
                                        </Button>
                                    </Dropdown>
                                </Form.Item>
                                <Form.Item>
                                    <Select
                                        placeholder="Select Location"
                                        onChange={this.feedSelected}
                                    >
                                        <Option value={0}>All</Option>
                                        {(this.state.feeds || []).map(feed => <Option value={feed.id}>{feed.site + " > " + feed.location}</Option>)}
                                    </Select>
                                </Form.Item>
                                <Form.Item>
                                    Report Type
                                    <Dropdown overlay={() => this.getReportTypeOptions("report")}>
                                        <Button style={{width: '-webkit-fill-available'}}>
                                            {report.filter.reportType ? report.filter.reportType : "Select"}
                                            <Icon
                                                type="down"/>
                                        </Button>
                                    </Dropdown>

                                </Form.Item>
                                <Form.Item>
                                    Report Format
                                    <Select defaultValue="csv" onChange={this.handleChangeReportType}>
                                        <Option value="csv">csv</Option>
                                        {/*<Option value="json">json</Option>*/}
                                    </Select>
                                </Form.Item>
                                <Form.Item>
                                    <Button type="primary" htmlType="submit" onClick={this.downloadReport} block
                                            loading={this.state.downloading}>
                                        Download
                                    </Button>
                                </Form.Item>
                            </Form>
                        </Card>
                    </Col>
                </Row>
            </div>
        )
    }
}