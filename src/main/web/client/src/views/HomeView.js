import React, {Component} from "react";
import {Col, Row, Statistic, TimePicker, DatePicker, Button, Icon, message, Card, Menu, Dropdown, Select} from "antd";
import DashboardService from "../services/DashboardService";
import moment from 'moment';
import classnames from 'classnames';
import {Bar, Pie, Line} from 'react-chartjs-2';
import * as name from "chartjs-plugin-colorschemes";
import cloneDeep from 'lodash/cloneDeep';

const {Option} = Select;

export default class HomeView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            selectedCustomDateRange: "Today",
            selectedXAxisOption: "Hourly",
            atcc: {
                chartData: {
                    labels: [],
                    datasets: []
                }
            }
        };

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


    selectDateRange(selectedCustomDateRange) {
        this.setState({selectedCustomDateRange}, () => {
            this.refresh();
        });

    }

    selectXAxisOption(selectedXAxisOption) {
        this.setState({selectedXAxisOption}, () => {
            this.refresh();
        });
    }

    refresh() {
        let {selectedCustomDateRange, selectedXAxisOption} = this.state;
        let fromToDate = DashboardService.extractFromToDate(selectedCustomDateRange);
        this.getAtccVehicleCount(fromToDate.from_date, fromToDate.to_date, selectedXAxisOption);
    }


    getAtccVehicleCount(from_date, to_date, xAxis) {


        let {atcc} = this.state;

        DashboardService.getAtccVehicleCount(from_date, to_date, xAxis).then(resposne => {

            let rawData = resposne.data;
            if (rawData && rawData.length > 0) {
                //let labelDates = DashboardService.enumerateDaysBetweenDates(from_date, to_date);
                let labelDates = []

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
            } else {
                atcc = {
                    chartData: {
                        labels: [],
                        datasets: []
                    }
                }
            }

            this.setState({atcc});
        }).catch(error => {
            console.log(error);
        });
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
            </Menu>
        );
    }

    getXAxisOptions() {
        return (
            <Menu>

                <Menu.Item key="1"
                           onClick={() => this.selectXAxisOption("Hourly")}>
                    Hourly
                </Menu.Item>
                <Menu.Item key="5"
                           onClick={() => this.selectXAxisOption("Daily")}>
                    Daily
                </Menu.Item>
            </Menu>
        );
    }

    getBarChartOptions() {
        let yAxisScaleLabel = "Day";
        if (this.state.selectedXAxisOption === "Hourly") {
            yAxisScaleLabel = "Hours(24-hour)"
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
                        labelString: "Vehicles"
                    }
                }]
            }
        };
        return options;
    }

    render() {
        let {selectedCustomDateRange, selectedXAxisOption} = this.state;

        const menu = this.getDateRangeOptions();
        const XAxisOptions = this.getXAxisOptions();
        const barChartOptions = this.getBarChartOptions();

        return (
            <div>
                <div>
                    <Card title={<div>ATCC
                        &nbsp;
                        <Dropdown overlay={menu}>
                            <Button>
                                {selectedCustomDateRange ? selectedCustomDateRange : "Select"} <Icon type="down"/>
                            </Button>
                        </Dropdown>
                        &nbsp;<Dropdown overlay={XAxisOptions}>
                            <Button>
                                {selectedXAxisOption ? selectedXAxisOption : "Select"} <Icon type="down"/>
                            </Button>
                        </Dropdown>
                    </div>}>
                        <Bar data={this.state.atcc.chartData} options={barChartOptions}/>
                    </Card>
                </div>
            </div>
        )
    }
}