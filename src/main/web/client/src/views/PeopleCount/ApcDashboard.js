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
    Select, Table
} from "antd";
import ApcDashboardService from "../../services/ApcDashboardService";
import { Line, Doughnut} from 'react-chartjs-2';

const {RangePicker} = DatePicker;
const {Column} = Table;

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
            },
            peakhour: {
                filter: {
                    selectedCustomDateRange: "Today",

                    fromDate: {},
                    toDate: {}
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            },
            peakHourEvents: []
        }

        this.getApcPeopleCount = this.getApcPeopleCount.bind(this);
        this.getApcPeakHour = this.getApcPeakHour.bind(this);
        this.getBarChartOptions = this.getBarChartOptions.bind(this);
        this.selectDateRange = this.selectDateRange.bind(this);
        this.selectXAxisOption = this.selectXAxisOption.bind(this);
        this.refresh = this.refresh.bind(this);
        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);
        this.getXAxisOptions = this.getXAxisOptions.bind(this);
        this.handleDateRangeChange = this.handleDateRangeChange.bind(this);
        this.refreshPeakHour = this.refreshPeakHour.bind(this);
        this.refreshApcChart = this.refreshApcChart.bind(this);
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
        if (graphName === "peakhour") {
            this.setState({[graphName]: graph, isOpencustomDateRangeModal}, () => {
                this.refreshPeakHour();
            });
        } else if (graphName === "apc") {
            this.setState({[graphName]: graph, isOpencustomDateRangeModal}, () => {
                this.refreshApcChart();
            });
        }

    }

    selectXAxisOption(graphName, selectedXAxisOption) {
        let graph = this.state[graphName];
        graph.filter.selectedXAxisOption = selectedXAxisOption;
        if (graphName === "peakhour") {
            this.setState({[graphName]: graph}, () => {
                this.refreshPeakHour();
            });
        } else if (graphName === "apc") {
            this.setState({[graphName]: graph}, () => {
                this.refreshApcChart();
            });
        }
    }

    refresh() {
        this.refreshPeakHour();
        this.refreshApcChart();
    }

    refreshApcChart() {
        this.getApcPeopleCount(this.state.apc.filter.fromDate, this.state.apc.filter.toDate, this.state.apc.filter.selectedXAxisOption);

    }

    refreshPeakHour() {
        this.getApcPeakHour(this.state.peakhour.filter.fromDate, this.state.peakhour.filter.toDate);

    }

    getApcPeakHour(from_date, to_date) {
        let {peakhour} = this.state;
        peakhour.chartData = {
            labels: [],
            datasets: []
        }
        ApcDashboardService.getApcPeakHour(from_date, to_date).then(response => {
            let rawData = response.data;
            if (rawData && rawData.length > 0) {
                let labelDuration = [];
                let percentageOfPeopleCount = [];
                for (let i in rawData) {
                    labelDuration.push(rawData[i].duration);
                    percentageOfPeopleCount.push(rawData[i].peopleCountPercentage);
                }
                peakhour.chartData.labels = labelDuration;
                let durationIndex = 0;
                let dataSet = {
                    data: [],
                    backgroundColor: [],
                    hoverBackgroundColor: []
                };
                for (let j in percentageOfPeopleCount) {

                    let color = ApcDashboardService.getColor(durationIndex);

                    dataSet.backgroundColor.push(color);
                    dataSet.hoverBackgroundColor.push("black");
                    dataSet.data.push(percentageOfPeopleCount[j]);

                    durationIndex++;
                }
                peakhour.chartData.datasets.push(dataSet);


            }
            this.setState({peakhour, peakHourEvents: rawData});
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

                    labelDates.push(rawData[i].date)
                    peopleCount.push(rawData[i].peopleCount);
                }
                apc.chartData.labels = labelDates;
                let dataSet = {
                    label: "No Of People",
                    data: [],
                    backgroundColor: '#e83e65'
                };
                for (let j in peopleCount) {

                    dataSet.data.push(peopleCount[j]);


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
            },
            plugins: {
                datalabels: {
                    display: true,

                }
            }
        };
        return options;
    }

    render() {
        let {apc, peakhour} = this.state;
        let peakHourEvents = this.state.peakHourEvents;
        const apcChartOptions = this.getBarChartOptions("apc");
        return (
            <div style={{background: '#ECECEC', padding: '5px'}}>
                <Row>


                    <Card title={<div>Peak Hour
                        &nbsp;
                        <Dropdown overlay={() => this.getDateRangeOptions("peakhour")}>
                            <Button>
                                {peakhour.filter.selectedCustomDateRange ? peakhour.filter.selectedCustomDateRange : "Select"}
                                <Icon
                                    type="down"/>
                            </Button>
                        </Dropdown>
                    </div>
                    }>
                        <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>

                            <Doughnut data={peakhour.chartData} options={{
                                title: {
                                    display: true,
                                    text: 'Peak Hour'
                                },
                                maintainAspectRatio: true,

                                plugins: {
                                    datalabels: {
                                        display: true,
                                        color: '#fff',
                                        anchor: 'end',
                                        align: 'start',
                                        offset: -10,
                                        borderWidth: 2,
                                        borderColor: '#fff',
                                        borderRadius: 5,
                                        backgroundColor: (context) => {
                                            return context.dataset.backgroundColor;
                                        },
                                        font: {
                                            weight: 'bold',
                                            size: '10'
                                        },
                                        formatter: (item, context) => {
                                            return item + " % ";
                                        }
                                    }
                                },
                                rotation: 1 * Math.PI,
                                circumference: 1 * Math.PI,


                            }}/>

                        </Col>
                        <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>

                            <div>
                                <Table dataSource={peakHourEvents} bordered={true} size={"small"} pagination={false}>
                                    <Column title="Duration" dataIndex="duration" key="duration"
                                            render={duration => duration} />
                                    <Column title="No Of People" dataIndex="peopleCount" key="peopleCount"
                                            render={peopleCount => peopleCount}/>
                                </Table>
                            </div>


                        </Col>
                    </Card>


                </Row>
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
                        < Line data={apc.chartData} options={apcChartOptions}/>

                    </Card>
                </div>


            </div>
        )
    }

}