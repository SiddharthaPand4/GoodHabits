import React, {Component} from "react";
import {Col, Row, Statistic, TimePicker, DatePicker, Button, Icon, message, Card, Modal,Menu, Dropdown, Select} from "antd";
import ApcDashboardService from "../../services/ApcDashboardService";
import CommonService from "../../services/CommonService";
import Moment from 'moment';
import classnames from 'classnames';
import {Bar, Pie, Line} from 'react-chartjs-2';
import * as name from "chartjs-plugin-colorschemes";
import cloneDeep from 'lodash/cloneDeep';


const {Option} = Select;
const { RangePicker } = DatePicker;

export default class ApcDashboard extends Component {

        constructor(props) {
            super(props);
            this.state = {
                isOpencustomDateRangeModal:"",
                atcc: {
                    filter: {
                        selectedCustomDateRange: "Today",
                        selectedXAxisOption: "Hourly",
                        fromDate:{},
                        toDate:{}
                    },
                    chartData: {
                        labels: [],
                        datasets: []
                    }
                },incident: {
                    filter: {
                        selectedCustomDateRange: "Today",
                        selectedXAxisOption: "Hourly",
                        fromDate:{},
                        toDate:{}
                    },
                    chartData: {
                        labels: [],
                        datasets: []
                    }
                },
            };

           // this.getIncidentVehicleCount = this.getIncidentVehicleCount.bind(this);
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
    showCustomDateRangeModal(graphName){
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


        if(selectedCustomDateRangeEnum=== "Custom"){
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

    refresh(){
        this.getApcPeopleCount(this.state.atcc.filter.fromDate, this.state.atcc.filter.toDate, this.state.atcc.filter.selectedXAxisOption);
    }

    getApcPeopleCount(from_date, to_date, xAxis) {
            let {atcc} = this.state;
            atcc.chartData = {
                labels: [],
                datasets: []
            };
            ApcDashboardService.getApcPeopleCount(from_date, to_date, xAxis).then(resposne => {

                let rawData = resposne.data;
                if (rawData && rawData.length > 0) {
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

                        let color = ApcDashboardService.getColor(vehicleTypeIndex);
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



    handleDateRangeChange(dates, dateString){

        let startDate = dates[0].toDate();
        let endDate = dates[1].toDate();
        this.setState( () => {
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
                <Menu.Item  onClick={() =>this.showCustomDateRangeModal(graphName)}>
                    Custom
                </Menu.Item>

            </Menu>
        );
    }

    getBarChartOptions(chartName) {
        let yAxisScaleLabel = "Day";
        if (this.state[chartName].filter.selectedXAxisOption === "Hourly") {
            yAxisScaleLabel = "Hours(24-hour)"
        }
        let yAxisLabel = "No Of People";
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

                    <Modal
                     onCancel={this.handleCancel}
                      title="Custom Date Range"
                      visible={this.state.isOpencustomDateRangeModal ? true : false}
                      footer={[
                      ]}

                    >
                         <RangePicker
                          onChange={(changedDateRange)=> this.selectDateRange(this.state.isOpencustomDateRangeModal, "Custom", changedDateRange)} />
                    </Modal>
                  </div>
                <div>
                    <Card title={<div>APC
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
                    </div>
                    </div>
                )
            }

}