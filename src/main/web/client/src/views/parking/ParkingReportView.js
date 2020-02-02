import React, {Component} from "react";
import {Button, Card, DatePicker, Dropdown, Icon, Menu, Modal, Select} from "antd";
import DashboardService from "../../services/DashboardService";
import CommonService from "../../services/CommonService";
import Moment from 'moment';

const {Option} = Select;
const {RangePicker} = DatePicker;

export default class HomeView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isOpencustomDateRangeModal: "",
            atcc: {
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
        };

        this.selectDateRange = this.selectDateRange.bind(this);
        this.selectXAxisOption = this.selectXAxisOption.bind(this);

        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);
        this.getXAxisOptions = this.getXAxisOptions.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange() {

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
            this.refresh();
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
                       onClick={() => this.selectXAxisOption(graphName, "Hourly")}>
                Hourly
            </Menu.Item>
            <Menu.Item key="5"
                       onClick={() => this.selectXAxisOption(graphName, "Daily")}>
                Daily
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
                <div>
                    <Card title={<div>Reports:
                        &nbsp;
                        <Dropdown overlay={() => this.getDateRangeOptions("atcc")}>
                            <Button>
                                {atcc.filter.selectedCustomDateRange ? atcc.filter.selectedCustomDateRange : "Select"}
                                <Icon
                                    type="down"/>
                            </Button>
                        </Dropdown>
                        &nbsp;<Dropdown overlay={() => this.getXAxisOptions("atcc")}>
                            <Button>
                                {atcc.filter.selectedXAxisOption ? atcc.filter.selectedXAxisOption : "Select"} <Icon
                                type="down"/>
                            </Button>
                        </Dropdown>
                        <Select defaultValue="inout" style={{ width: 200 }} onChange={this.handleChange}>
                            <Option value="inout">In/Out Report</Option>
                            <Option value="occupancy">Occupancy Report</Option>
                        </Select>
                        <Select defaultValue="csv" style={{ width: 120 }} onChange={this.handleChange}>
                            <Option value="csv">CSV</Option>
                            <Option value="xml">XML</Option>
                            <Option value="json">Json</Option>
                        </Select>
                        <Button>Download</Button>
                    </div>}>
                    </Card>
                    <br/>
                </div>
            </div>
        )
    }
}