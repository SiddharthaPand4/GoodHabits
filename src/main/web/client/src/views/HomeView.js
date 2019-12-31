import React, {Component} from "react";
import {Col, Row, Statistic, TimePicker, DatePicker, Button, Icon, message, Card, Menu, Dropdown, Select} from "antd";
import DashboardService from "../services/DashboardService";
import CommonService from "../services/CommonService";
import Moment from 'moment';
import classnames from 'classnames';
import {Bar, Pie, Line} from 'react-chartjs-2';
import * as name from "chartjs-plugin-colorschemes";
import cloneDeep from 'lodash/cloneDeep';

const {Option} = Select;

export default class HomeView extends Component {

    constructor(props) {
        super(props);
        this.state = {

            atcc: {
                filter: {
                    selectedCustomDateRange: "Today",
                    selectedXAxisOption: "Hourly",
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            }, incident: {
                filter: {
                    selectedCustomDateRange: "Today",
                    selectedXAxisOption: "Hourly",
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            }
        };

        this.getIncidentVehicleCount = this.getIncidentVehicleCount.bind(this);
        this.getAtccVehicleCount = this.getAtccVehicleCount.bind(this);
        this.getBarChartOptions = this.getBarChartOptions.bind(this);
        this.selectDateRange = this.selectDateRange.bind(this);
        this.selectXAxisOption = this.selectXAxisOption.bind(this);
        this.refresh = this.refresh.bind(this);
        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);
        this.getXAxisOptions = this.getXAxisOptions.bind(this);


    }

    componentDidMount() {
        this.refresh();
    }


    selectDateRange(graphName, selectedCustomDateRange) {
        let graph = this.state[graphName];
        graph.filter.selectedCustomDateRange = selectedCustomDateRange;
        this.setState({[graphName]: graph}, () => {
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
        let fromToDate = DashboardService.extractFromToDate(this.state.atcc.filter.selectedCustomDateRange);
        this.getAtccVehicleCount(fromToDate.from_date, fromToDate.to_date, this.state.atcc.filter.selectedXAxisOption);

        fromToDate = DashboardService.extractFromToDate(this.state.incident.filter.selectedCustomDateRange);
        this.getIncidentVehicleCount(fromToDate.from_date, fromToDate.to_date, this.state.incident.filter.selectedXAxisOption);
    }


    getAtccVehicleCount(from_date, to_date, xAxis) {
        let {atcc} = this.state;
        atcc.chartData = {
            labels: [],
            datasets: []
        };
        DashboardService.getAtccVehicleCount(from_date, to_date, xAxis).then(resposne => {

            let rawData = resposne.data;
            if (rawData && rawData.length > 0) {
                //let labelDates = DashboardService.enumerateDaysBetweenDates(from_date, to_date);
                let labelDates = [];

                let rawDataByVehicleData = [];
                for (let i in rawData) {

                    if (!labelDates.includes(rawData[i].date)) {
                        labelDates.push(rawData[i].date)
                    }

                    if (!rawDataByVehicleData[rawData[i].vehicleType]) {
                        rawDataByVehicleData[rawData[i].vehicleType] = {};
                    }
                    if (!rawDataByVehicleData[rawData[i].vehicleType][rawData[i].date]) {
                        rawDataByVehicleData[rawData[i].vehicleType][rawData[i].date] = rawData[i];
                    }
                }
                atcc.chartData.labels = labelDates;
                let vehicleTypeIndex = 0;
                for (let vehicleType in rawDataByVehicleData) {

                    let color = DashboardService.getColor(vehicleTypeIndex);
                    let dataSet = {
                        label: vehicleType,
                        data: [],
                        backgroundColor: color
                    };

                    for (let i in labelDates) {
                        if (rawDataByVehicleData[vehicleType][labelDates[i]]) {
                            dataSet.data.push(rawDataByVehicleData[vehicleType][labelDates[i]].vehicleCount);
                        } else {
                            dataSet.data.push(0);
                        }
                    }
                    atcc.chartData.datasets.push(dataSet);
                    vehicleTypeIndex++;
                }
            }
            this.setState({atcc});
        }).catch(error => {
            console.log(error);
        });
    }


    getIncidentVehicleCount(from_date, to_date, xAxis) {
        let {incident} = this.state;
        incident.chartData = {
            labels: [],
            datasets: []
        };
        DashboardService.getIncidentVehicleCount(from_date, to_date, xAxis).then(resposne => {

            let helmetMissingIncidents = resposne.data.helmetMissingIncidents;
            let reverseDirectionIncidents = resposne.data.reverseDirectionIncidents;

            //Get all unique labels(timestamps) from both type of incidents
            let labels = [];
            for (let i in reverseDirectionIncidents) {
                if (xAxis === "Hourly") {
                    reverseDirectionIncidents[i].date = parseInt(reverseDirectionIncidents[i].date);
                }
                if (!labels.includes(reverseDirectionIncidents[i].date)) {
                    labels.push(reverseDirectionIncidents[i].date);
                }
            }

            for (let i in helmetMissingIncidents) {
                if (xAxis === "Hourly") {
                    helmetMissingIncidents[i].date = parseInt(helmetMissingIncidents[i].date);
                }
                if (!labels.includes(helmetMissingIncidents[i].date)) {
                    labels.push(helmetMissingIncidents[i].date);
                }
            }

            // sort the labels, as it is a timeseries
            if (xAxis === "Hourly") {
                labels.sort((a, b) => a - b);
            }
            if (xAxis === "Daily") {
                labels.sort((a, b) => new Moment(a).format('YYYY-MM-DD') - new Moment(b).format('YYYY-MM-DD'))
            }

            //prepare dataset
            let helmetMissingDataset = {
                label: "Without Helmet",
                data: [],
                backgroundColor: DashboardService.getColor(0)
            };
            let reverseDirectionDataset = {
                label: "Reverse Direction",
                data: [],
                backgroundColor: DashboardService.getColor(2)
            };

            //fill the value of dataset for each label
            let dataValue = 0;
            let index = -1;
            for (let i in labels) {
                dataValue = 0;
                index = CommonService.findIndex(helmetMissingIncidents, 'date', labels[i]);
                if (index > -1) {
                    dataValue = helmetMissingIncidents[index].vehicleCount;
                }
                helmetMissingDataset.data.push(dataValue);

                dataValue = 0;
                index = CommonService.findIndex(reverseDirectionIncidents, 'date', labels[i]);
                if (index > -1) {
                    dataValue = reverseDirectionIncidents[index].vehicleCount;
                }
                reverseDirectionDataset.data.push(dataValue);
            }

            //finalize it with setState
            incident.chartData.labels = labels;
            incident.chartData.datasets.push(helmetMissingDataset);
            incident.chartData.datasets.push(reverseDirectionDataset);
            this.setState({incident});
        }).catch(error => {
            this.setState({incident});
            console.log(error);
        });
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
            </Menu>
        );
    }

    getBarChartOptions(chartName) {
        let yAxisScaleLabel = "Day";
        if (this.state[chartName].filter.selectedXAxisOption === "Hourly") {
            yAxisScaleLabel = "Hours(24-hour)"
        }
        let yAxisLabel = "Vehicles";
        if (chartName === "incident") {
            yAxisLabel = "Challans"
        }

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
        let {atcc, incident} = this.state;
        const atccChartOptions = this.getBarChartOptions("atcc");
        const incidentChartOptions = this.getBarChartOptions("incident");
        return (
            <div>
                <div>
                    <Card title={<div>ATCC
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
                    </div>}>
                        <Line data={atcc.chartData} options={atccChartOptions}/>

                    </Card>
                    <br/>
                    <Card title={<div>Incidents
                        &nbsp;
                        <Dropdown overlay={() => this.getDateRangeOptions("incident")}>
                            <Button>
                                {incident.filter.selectedCustomDateRange ? incident.filter.selectedCustomDateRange : "Select"}
                                <Icon
                                    type="down"/>
                            </Button>
                        </Dropdown>
                        &nbsp;<Dropdown overlay={() => this.getXAxisOptions("incident")}>
                            <Button>
                                {incident.filter.selectedXAxisOption ? incident.filter.selectedXAxisOption : "Select"}
                                <Icon
                                    type="down"/>
                            </Button>
                        </Dropdown>
                    </div>}>
                        <Line data={incident.chartData} options={incidentChartOptions}/>
                    </Card>
                </div>
            </div>
        )
    }
}