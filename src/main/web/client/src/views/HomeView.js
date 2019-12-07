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
            selectedCustomDateRange: "",
            atcc: {
                chartData: {
                    labels: [],
                    datasets: []
                }
            }
        };

        this.getAtccVehicleCount = this.getAtccVehicleCount.bind(this);


    }

    componentDidMount() {
        this.selectDateRange("This week");
    }


    selectDateRange(selectedCustomDateRange) {
        this.setState({selectedCustomDateRange});

        let fromToDate = DashboardService.extractFromToDate(selectedCustomDateRange);

        this.getAtccVehicleCount(fromToDate.from_date, fromToDate.to_date);
    }


    getAtccVehicleCount(from_date, to_date) {


        let {atcc} = this.state;

        DashboardService.getAtccVehicleCount(from_date, to_date).then(resposne => {
            let rawData = resposne.data;
            let labelDates = DashboardService.enumerateDaysBetweenDates(from_date, to_date);
            atcc.chartData.labels = labelDates;


            let rawDataByVehicleData = [];
            for (let i in rawData) {
                if (!rawDataByVehicleData[rawData[i].vehicleType]) {
                    rawDataByVehicleData[rawData[i].vehicleType] = {};
                }
                if (!rawDataByVehicleData[rawData[i].vehicleType][rawData[i].date]) {
                    rawDataByVehicleData[rawData[i].vehicleType][rawData[i].date] = rawData[i];
                }
            }
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
            this.setState({atcc});
        }).catch(error => {
            console.log(error);
        });
    }

    getMenuOptions() {
        return (
            <Menu>
                <Menu.Item key="1" onClick={() => this.selectDateRange("This week")}>
                    This week
                </Menu.Item>
                <Menu.Item key="2" onClick={() => this.selectDateRange("This month")}>
                    This month
                </Menu.Item>
                <Menu.Item key="2" onClick={() => this.selectDateRange("This quarter")}>
                    This quarter
                </Menu.Item>
                <Menu.Divider/>
                <Menu.Item key="5" onClick={() => this.selectDateRange("Last week")}>
                    Last week
                </Menu.Item>
                <Menu.Item key="6" onClick={() => this.selectDateRange("Last month")}>
                    Last month
                </Menu.Item>
                <Menu.Item key="7" onClick={() => this.selectDateRange("Last quarter")}>
                    Last quarter
                </Menu.Item>
                <Menu.Divider/>
                <Menu.Item key="9" onClick={() => this.selectDateRange("Second Last week")}>
                    Second Last week
                </Menu.Item>
                <Menu.Item key="10" onClick={() => this.selectDateRange("Second Last month")}>
                    Second Last month
                </Menu.Item>
                <Menu.Item key="11" onClick={() => this.selectDateRange("Second Last quarter")}>
                    Second Last quarter
                </Menu.Item>
            </Menu>
        );
    }

    getBarChartOptions() {
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
                position: 'right' // place legend on the right side of chart
            },
            scales: {
                xAxes: [{
                    stacked: true // this should be set to make the bars stacked
                }],
                yAxes: [{
                    stacked: true // this also..
                }]
            }
        };
        return options;
    }

    render() {
        let {selectedCustomDateRange} = this.state;

        const menu = this.getMenuOptions();
        const barChartOptions = this.getBarChartOptions();

        return (
            <div>
                <div>

                    <Dropdown overlay={menu}>
                        <Button>
                            {selectedCustomDateRange ? selectedCustomDateRange : "Select"} <Icon type="down"/>
                        </Button>
                    </Dropdown>
                    <Line data={this.state.atcc.chartData} options={barChartOptions}/>
                </div>


            </div>
        )
    }
}