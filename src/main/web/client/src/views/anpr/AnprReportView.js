import React, {Component} from "react";
import {Button, Card, DatePicker, Dropdown, Icon, Menu, Modal, Select, Row, Col, Form, Spin, message} from "antd";
import DashboardService from "../../services/DashboardService";
import ReportService from "../../services/ReportService";
import CommonService from "../../services/CommonService";
import moment from 'moment';
import { saveAs } from 'file-saver';
import FeedService from "../../services/FeedService";

const {Option} = Select;
const {RangePicker} = DatePicker;

export default class AnprReportView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            feedsList: [],
            isOpencustomDateRangeModal: "",
            atcc: {
                filter: {
                    feedId: 0,
                    selectedCustomDateRange: "Today",
                    selectedXAxisOption: "All Entry-Exit",
                    fromDate: moment().startOf('day').toDate(),
                    toDate: moment().endOf('day').toDate(),
                    reportType:"CSV",
                    filterType:"inout"
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            }, incident: {
                filter: {
                    selectedCustomDateRange: "Today",
                    selectedXAxisOption: "Hourly",
                    fromDate: {},
                    toDate: {}
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            },
            downloading:false,
        };

        this.selectDateRange = this.selectDateRange.bind(this);
        this.selectXAxisOption = this.selectXAxisOption.bind(this);

        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);
        this.getXAxisOptions = this.getXAxisOptions.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.downloadReport = this.downloadReport.bind(this);
        this.handleChangeReportType = this.handleChangeReportType.bind(this);
    }

    componentDidMount() {
        this.fetchFeedsList()
    }

    fetchFeedsList = async () => {
        try {
            const res = await FeedService.getFeeds()
            const feedsList = res.data
            this.setState({ feedsList })
        } catch(e) {
            console.log(e)
            message.error("Something Went Wrong ")
        }
    }

    feedSelected = value => {
        const atcc = {...this.state.atcc}
        atcc.filter.feedId = value
        this.setState({atcc})
    }


    handleChange(value) {

        let atcc= {...this.state.atcc}
        let filter= atcc.filter;
        filter.filterType= value;

        this.setState({atcc:atcc})
    }

    handleChangeReportType(value) {
        let atcc= {...this.state.atcc}
        let filter= atcc.filter;
        filter.reportType= value;
        this.setState({atcc:atcc})
    }

    showCustomDateRangeModal(graphName) {
        this.setState({
            isOpencustomDateRangeModal: graphName,
        });
    };

    handleCancel = e => {
    this.setState({
                      isOpencustomDateRangeModal: "",
                  });
};

selectDateRange(graphName, selectedCustomDateRangeEnum, selectedCustomDateRangeMoment) {
    let {isOpencustomDateRangeModal} = this.state;
    let graph = this.state[graphName];
    graph.filter.selectedCustomDateRange = selectedCustomDateRangeEnum;
    let fromToDate = DashboardService.extractFromToDate(graph.filter.selectedCustomDateRange, selectedCustomDateRangeMoment);
    graph.filter.fromDate = fromToDate.from_date;
    graph.filter.toDate = fromToDate.to_date;


    if (selectedCustomDateRangeEnum === "Custom") {
        isOpencustomDateRangeModal = ""
    }
    this.setState({[graphName]: graph, isOpencustomDateRangeModal}, () => {
        //  this.refresh();
    });

}

downloadReport(){
    this.setState({downloading:true})
    let filter= this.state.atcc.filter;
    var req={
        fromDateString: moment(filter.fromDate).format('YYYY-MM-DD HH:mm:ss'),
        toDateString: moment(filter.toDate).format('YYYY-MM-DD HH:mm:ss"'),
        xAxis: filter.selectedXAxisOption,
        reportType:filter.reportType,
        filterType:filter.filterType,
        feedId: filter.feedId
    }

    ReportService.getAnprReport(req).then(response => {
        this.setState({downloading: false});

    if(filter.reportType=="CSV"){
        saveAs(response.data, "anpr-events.csv");
    }
    else if(filter.reportType=="JSON"){
        saveAs(response.data, "anpr-events.json");
    }
    else if(filter.reportType=="EXCEL"){
        saveAs(response.data, "anpr-events.xlsx");
    }

}).catch(error => {
        this.setState({downloading: false});
});
}

selectXAxisOption(graphName, selectedXAxisOption) {
    let graph = this.state[graphName];
    graph.filter.selectedXAxisOption = selectedXAxisOption;
    this.setState({[graphName]: graph});
}

getXAxisOptions(graphName) {
    return (<Menu>

        <Menu.Item key="1"
    onClick={() => this.selectXAxisOption(graphName, "All Entry-Exit")}>
    All Entry-Exit
    </Menu.Item>
    <Menu.Item key="5"
    onClick={() => this.selectXAxisOption(graphName, "DayWise Summary")}>
    DayWise Summary
    </Menu.Item>
    </Menu>)
}

getDateRangeOptions(graphName) {
    return (
        <Menu>
        <Menu.Item key="1" onClick={() => this.selectDateRange(graphName, "Today")}>
    Today
    </Menu.Item>
    <Menu.Item key="2" onClick={() => this.selectDateRange(graphName, "Yesterday")}>
    Yesterday
    </Menu.Item>
    <Menu.Item key="3" onClick={() => this.selectDateRange(graphName, "This week")}>
    This week
    </Menu.Item>
    <Menu.Item key="4" onClick={() => this.selectDateRange(graphName, "Last week")}>
    Last week
    </Menu.Item>
    <Menu.Item key="5" onClick={() => this.selectDateRange(graphName, "This month")}>
    This month
    </Menu.Item>
    <Menu.Item key="6" onClick={() => this.selectDateRange(graphName, "This year")}>
    This year
    </Menu.Item>
    <Menu.Item key="7" onClick={() => this.selectDateRange(graphName, "Last year")}>
    Last year
    </Menu.Item>
    <Menu.Item onClick={() => this.showCustomDateRangeModal(graphName)}>
    Custom
    </Menu.Item>

    </Menu>
);
}

render() {
    let {atcc, incident} = this.state;
    const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;
    return (
        <div>
        <div>

        <Modal
    onCancel={this.handleCancel}
    title="Custom Date Range"
    visible={this.state.isOpencustomDateRangeModal ? true : false}
    footer={[]}
        >
        <RangePicker
    onChange={(changedDateRange) => this.selectDateRange(this.state.isOpencustomDateRangeModal, "Custom", changedDateRange)}/>
    </Modal>
    </div>
    <Row>
    <Col xl={{span: 8}} lg={{span: 6}} md={{span: 4}} sm={{span: 2}} xs={{span: 2}}/>
    <Col xl={{span: 8}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>
<Card title={<div>Anpr Report</div>}>
    <Form>
    <Form.Item>
    Select Date Range
    <Dropdown overlay={() => this.getDateRangeOptions("atcc")}>
<Button style={{width: '-webkit-fill-available'}}>
    {atcc.filter.selectedCustomDateRange ? atcc.filter.selectedCustomDateRange : "Select"}
<Icon
    type="down"/>
        </Button>
        </Dropdown>
        </Form.Item>

        <Form.Item>
        Report Type
    <Dropdown overlay={() => this.getXAxisOptions("atcc")}>
<Button style={{width: '-webkit-fill-available'}}>
    {atcc.filter.selectedXAxisOption ? atcc.filter.selectedXAxisOption : "Select"} <Icon
    type="down"/>
        </Button>
        </Dropdown>

        </Form.Item>
    {/*<Form.Item>

                                     <Select defaultValue="inout" onChange={this.handleChange}>
                                         <Option value="inout">In/Out Report</Option>
                                     </Select>

                                  </Form.Item> */}
<Form.Item>
    Report Format
    <Select defaultValue="CSV" onChange={this.handleChangeReportType}>
        <Option value="CSV">CSV</Option>
        <Option value="JSON">JSON</Option>
        {this.state.atcc.filter.selectedXAxisOption=="All Entry-Exit"? <Option value="EXCEL">EXCEL</Option>:null}
        </Select>

        </Form.Item>
        <Form.Item>
            <Select
                placeholder="Select Location"
                onChange={this.feedSelected}
            >
                {(this.state.feedsList || []).map(feed => <Option value={feed.id}>{feed.site + " > " + feed.location}</Option>)}
            </Select>
        </Form.Item>
        <Form.Item>
        <Button type="primary" htmlType="submit" onClick={this.downloadReport} block loading={this.state.downloading}>
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