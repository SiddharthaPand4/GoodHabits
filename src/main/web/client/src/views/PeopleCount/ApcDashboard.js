import React, {Component} from "react";
import {
    Col,
    Row,
    Statistic,
    TimePicker,
    DatePicker,
    Button,
    Icon,
    message,
    Card,
    Modal,
    Menu,
    Dropdown,
    Select
} from "antd";
import ApcDashboardService from "../../services/ApcDashboardService";
import CommonService from "../../services/CommonService";
import Moment from 'moment';
import classnames from 'classnames';
import {Bar, Pie, Line, Doughnut} from 'react-chartjs-2';
import * as name from "chartjs-plugin-colorschemes";
import cloneDeep from 'lodash/cloneDeep';

const {Option} = Select;
const {RangePicker} = DatePicker;

export default class ApcDashboard extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isOpencustomDateRangeModal: "",
            apc: {
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
            }
        }

        this.getApcPeopleCount = this.getApcPeopleCount.bind(this);
        this.getBarChartOptions = this.getBarChartOptions.bind(this);
        this.selectDateRange = this.selectDateRange.bind(this);
        this.selectXAxisOption = this.selectXAxisOption.bind(this);
        this.refresh = this.refresh.bind(this);
        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);
        this.getXAxisOptions = this.getXAxisOptions.bind(this);
        this.handleDateRangeChange = this.handleDateRangeChange.bind(this);

    }

    componentDidMount() {
        this.refresh();
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
        let fromToDate = ApcDashboardService.extractFromToDate(graph.filter.selectedCustomDateRange, selectedCustomDateRangeMoment);
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
        this.setState({[graphName]: graph}, () => {
            this.refresh();
        });
    }

    refresh() {
        this.getApcPeopleCount(this.state.apc.filter.fromDate, this.state.apc.filter.toDate, this.state.apc.filter.selectedXAxisOption);
        ApcDashboardService.getApcPeakHour(this.state.apc.filter.fromDate, this.state.apc.filter.toDate, this.state.apc.filter.selectedXAxisOption).then(response=>{
            let rawData = response.data;
        }).catch(error => {
            console.log(error);
        });
    }

    getApcPeopleCount(from_date, to_date, xAxis) {
        let {apc} = this.state;
        apc.chartData = {
            labels: [],
            datasets: []
        };
        ApcDashboardService.getApcPeopleCount(from_date, to_date, xAxis).then(response => {

            let rawData = response.data;
            if (rawData && rawData.length > 0) {
                let labelDates = [];
                let peopleCount = [];
                for (let i in rawData) {

                    if (!labelDates.includes(rawData[i].date)) {
                        labelDates.push(rawData[i].date)
                    }
                    if (!peopleCount[rawData[i].date]) peopleCount[rawData[i].date] = rawData[i];
                }
                apc.chartData.labels = labelDates;
                let dataSet = {
                    label: "No Of People",
                    data: [],
                    backgroundColor: '#e8003c'
                };
                for (let j in peopleCount) {

                    for (let i in labelDates) {
                        if (peopleCount[labelDates[i]]) {
                            dataSet.data.push(peopleCount[labelDates[i]].peopleCount);
                        } else {
                            dataSet.data.push(0);
                        }
                    }
                }
                apc.chartData.datasets.push(dataSet);
            }

            this.setState({apc});
        }).catch(error => {
            console.log(error);
        });

    }


    handleDateRangeChange(dates, dateString) {

        let startDate = dates[0].toDate();
        let endDate = dates[1].toDate();
        this.setState(() => {
            this.refresh();
        });
        console.log(dates, dateString);
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

    getBarChartOptions(chartName) {
        let yAxisScaleLabel = "Day";
        if (this.state[chartName].filter.selectedXAxisOption === "Hourly") {
            yAxisScaleLabel = "Hours(24-hour)";
        }
        let yAxisLabel = "No Of People";
        let options = {
            responsive: true,
            maintainAspectRatio: false,
            animation: {
                duration: 0
            },
            hover: {
                animationDuration: 0
            },
            responsiveAnimationDuration: 0,
            legend: {
                position: 'right'
            },
            scales: {
                xAxes: [{
                    stacked: true,
                    ticks: {
                        beginAtZero: true
                    }, scaleLabel: {
                        display: true,
                        labelString: yAxisScaleLabel
                    }
                }],
                yAxes: [{
                    stacked: true,
                    ticks: {
                        beginAtZero: true
                    }, scaleLabel: {
                        display: true,
                        labelString: yAxisLabel
                    }
                }]
            }
        };
        return options;
    }

    render() {
        let {apc} = this.state;
        const apcChartOptions = this.getBarChartOptions("apc");
        const data = {
            labels: [
                'Red',
                'Green',
                'Yellow'
            ],
            datasets: [{
                data: [300, 50, 100],
                backgroundColor: [
                    '#FF6384',
                    '#36A2EB',
                    '#FFCE56'
                ],
                hoverBackgroundColor: [
                    '#FF6384',
                    '#36A2EB',
                    '#FFCE56'
                ]
            }]
        };
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
                    <Card title={<div>People Counting
                        &nbsp;
                        <Dropdown overlay={() => this.getDateRangeOptions("apc")}>
                            <Button>
                                {apc.filter.selectedCustomDateRange ? apc.filter.selectedCustomDateRange : "Select"}
                                <Icon
                                    type="down"/>
                            </Button>
                        </Dropdown>
                        &nbsp;<Dropdown overlay={() => this.getXAxisOptions("apc")}>
                            <Button>
                                {apc.filter.selectedXAxisOption ? apc.filter.selectedXAxisOption : "Select"} <Icon
                                type="down"/>
                            </Button>
                        </Dropdown>
                    </div>}>
                        <Line data={apc.chartData} options={apcChartOptions}/>

                    </Card>
                </div>
                <div>
                        <Doughnut data={data} />
                </div>
            </div>
        )
    }

}