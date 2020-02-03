import React, {Component} from "react";
import {Line, Pie, Doughnut} from 'react-chartjs-2';
import {
    Col,
    Row,
    Button,
    Icon,
    Card,
    Modal,
    Menu,
    Dropdown,
    Select,
    DatePicker, Skeleton
} from "antd";
import ApmsService from "../../services/ApmsService";
import {cloneDeep} from 'lodash';
import DashboardService from "../../services/DashboardService";
import CommonService from "../../services/CommonService";
import * as name from "chartjs-plugin-colorschemes";
import Moment from 'moment';
import classnames from 'classnames';

const {Option} = Select;
const {RangePicker} = DatePicker;

let fo_data = {
    labels: [
        'Occupied',
        'Free'
    ],
    datasets: [{
        data: [0, 0],
        backgroundColor: [DashboardService.getColor(0), DashboardService.getColor(2)]
    }]
};


let entryexit_data = {
    labels: [
        'Car - In',
        'Bike - In',
    ],
    datasets: [{
        data: [44, 0]
    }]
};

export default class ParkingDashboardView extends Component {


    constructor(props) {
        super(props);

        const baseDate = Moment();
        this.state = {
            loading: {
                parkingEventData: false,
                stats: false
            },
            isOpencustomDateRangeModal: "",
            fo_data: fo_data,
            cb_data: {},
            entryexit_data: entryexit_data,
            parkingEventData: {
                filter: {
                    selectedCustomDateRange: "Today",
                    selectedXAxisOption: "Hourly",
                    fromDate: baseDate.startOf('day').toDate(),
                    toDate: baseDate.endOf('day').toDate()
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            }

        };

        this.refresh = this.refresh.bind(this);
        this.getParkingSlotStats = this.getParkingSlotStats.bind(this);
        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);

        this.getParkingVehicleCount = this.getParkingVehicleCount.bind(this);
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

    handleCancel = e => {
        this.setState({
            isOpencustomDateRangeModal: "",
        });
    };

    handleDateRangeChange(dates, dateString) {

        let startDate = dates[0].toDate();
        let endDate = dates[1].toDate();
        this.setState(() => {
            this.refresh();
        });
        console.log(dates, dateString);
    }

    refresh() {
        this.getParkingSlotStats();
        this.getParkingVehicleCount(this.state.parkingEventData.filter.fromDate, this.state.parkingEventData.filter.toDate, this.state.parkingEventData.filter.selectedXAxisOption);
    }

    selectXAxisOption(selectedXAxisOption) {
        let parkingEventData = this.state.parkingEventData;
        parkingEventData.filter.selectedXAxisOption = selectedXAxisOption;
        this.setState({parkingEventData: parkingEventData}, () => {
            this.refresh();
        });
    }

    getXAxisOptions() {
        return (<Menu>
            <Menu.Item key="1"
                       onClick={() => this.selectXAxisOption("Hourly")}>
                Hourly
            </Menu.Item>
            <Menu.Item key="5"
                       onClick={() => this.selectXAxisOption("Daily")}>
                Daily
            </Menu.Item>
        </Menu>)
    }

    selectDateRange(selectedCustomDateRangeEnum, selectedCustomDateRangeMoment) {
        let {isOpencustomDateRangeModal} = this.state;
        let parkingEventData = this.state.parkingEventData;
        parkingEventData.filter.selectedCustomDateRange = selectedCustomDateRangeEnum;
        let fromToDate = DashboardService.extractFromToDate(parkingEventData.filter.selectedCustomDateRange, selectedCustomDateRangeMoment);
        parkingEventData.filter.fromDate = fromToDate.from_date;
        parkingEventData.filter.toDate = fromToDate.to_date;


        if (selectedCustomDateRangeEnum === "Custom") {
            isOpencustomDateRangeModal = false
        }
        this.setState({parkingEventData: parkingEventData, isOpencustomDateRangeModal}, () => {
            this.refresh();
        });

    }

    getParkingVehicleCount(from_date, to_date, xAxis) {
        let {parkingEventData, loading} = this.state;
        parkingEventData.chartData = {
            labels: [],
            datasets: []
        };
        loading.parkingEventData = true;
        this.setState({loading});
        DashboardService.getParkingVehicleCount(from_date, to_date, xAxis).then(resposne => {

            let checkInEvents = resposne.data.checkInEvents;
            let checkOutEvents = resposne.data.checkOutEvents;

            //Get all unique labels(timestamps) from both type of incidents
            let labels = [];
            for (let i in checkOutEvents) {
                if (xAxis === "Hourly") {
                    checkOutEvents[i].date = parseInt(checkOutEvents[i].date);
                }
                if (!labels.includes(checkOutEvents[i].date)) {
                    labels.push(checkOutEvents[i].date);
                }
            }

            for (let i in checkInEvents) {
                if (xAxis === "Hourly") {
                    checkInEvents[i].date = parseInt(checkInEvents[i].date);
                }
                if (!labels.includes(checkInEvents[i].date)) {
                    labels.push(checkInEvents[i].date);
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
            let checkInDataset = {
                label: "Check In",
                data: [],
                backgroundColor: DashboardService.getColor(0)
            };
            let checkOutDataset = {
                label: "Check Out",
                data: [],
                backgroundColor: DashboardService.getColor(2)
            };

            //fill the value of dataset for each label
            let dataValue = 0;
            let index = -1;
            for (let i in labels) {
                dataValue = 0;
                index = CommonService.findIndex(checkInEvents, 'date', labels[i]);
                if (index > -1) {
                    dataValue = checkInEvents[index].vehicleCount;
                }
                checkInDataset.data.push(dataValue);

                dataValue = 0;
                index = CommonService.findIndex(checkOutEvents, 'date', labels[i]);
                if (index > -1) {
                    dataValue = checkOutEvents[index].vehicleCount;
                }
                checkOutDataset.data.push(dataValue);
            }

            //finalize it with setState
            parkingEventData.chartData.labels = labels;
            parkingEventData.chartData.datasets.push(checkInDataset);
            parkingEventData.chartData.datasets.push(checkOutDataset);
            loading.parkingEventData = false;
            this.setState({parkingEventData, loading});
        }).catch(error => {
            loading.parkingEventData = false;
            this.setState({parkingEventData, loading});
            console.log(error);
        });
    }

    getParkingSlotStats() {
        let {fo_data, loading} = this.state;
        loading.stats = true;
        this.setState({loading});
        ApmsService.getParkingSlotStats().then(response => {
            let statsData = response.data;
            fo_data.datasets[0].data[0] = statsData.totalSlots - statsData.freeSlots;
            fo_data.datasets[0].data[1] = statsData.freeSlots;


            let cb_data = {
                label: 'Car',
                labels: ['Parked Slots', 'Free Slots'],
                datasets: []
            };

            let dataset = {
                data: [statsData.carsParked, statsData.carSlots - statsData.carsParked],
                label: 'Car',
                labels: ["Parked Slots", "Free Slots"],
                backgroundColor: [DashboardService.getColor(0), DashboardService.getColor(2)]
            };
            cb_data.datasets.push(cloneDeep(dataset));

            dataset = {
                label: 'Bike',
                data: [statsData.bikesParked, statsData.bikeSlots - statsData.bikesParked],
                labels: ["Parked Slots", "Free Slots"],
                backgroundColor: [DashboardService.getColor(0), DashboardService.getColor(2)]
            };
            cb_data.datasets.push(cloneDeep(dataset));

            loading.stats = false;
            this.setState({fo_data, cb_data, loading});
        }).catch(error => {
            loading.stats = false;
            this.setState({loading});
            console.log(error);
        })
    }

    getBarChartOptions(chartName) {
        let yAxisScaleLabel = "Day";
        if (this.state[chartName].filter.selectedXAxisOption === "Hourly") {
            yAxisScaleLabel = "Hours(24-hour)"
        }
        let yAxisLabel = "Vehicles";
        if (chartName === "parkingEventData") {
            yAxisLabel = "Entries"
        }

        return {
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

    }

    showCustomDateRangeModal() {
        this.setState({
            isOpencustomDateRangeModal: true,
        });
    };

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
                <Menu.Item onClick={() => this.showCustomDateRangeModal()}>
                    Custom
                </Menu.Item>

            </Menu>
        );
    }

    render() {
        let {fo_data, cb_data, parkingEventData, loading} = this.state;
        const parkingEventsChartOptions = this.getBarChartOptions("parkingEventData");
        return (<div>

            {loading.stats ? <Skeleton active/> :
                <Row>
                    <Col md={8}>
                        <Card>
                            <Pie data={fo_data} options={{
                                title: {
                                    display: true,
                                    text: 'Free/Occupied'
                                }
                            }}/>
                        </Card>
                    </Col>
                    <Col md={8}>
                        <Card><Doughnut data={cb_data} options={{
                            circumference: Math.PI,
                            rotation: Math.PI,
                            title: {
                                display: true,
                                text: ' Car/Bike'
                            },
                            tooltips: {
                                callbacks: {
                                    title: function (item, data) {
                                        return data.datasets[item[0].datasetIndex].label;
                                    },
                                    label: function (item, data) {
                                        let label = data.datasets[item.datasetIndex].labels[item.index];
                                        let value = data.datasets[item.datasetIndex].data[item.index];
                                        return label + ': ' + value;
                                    }
                                }
                            }
                        }}/>
                        </Card>


                    </Col>
                    <Col md={8}>
                        <Card>
                            <Pie data={entryexit_data} options={{
                                title: {
                                    display: true,
                                    text: 'In Car/Bike'
                                }
                            }}/>
                        </Card>
                    </Col>
                </Row>

            }

            <br/>

            <Card title={<div>Parkings
                &nbsp;
                <Dropdown overlay={() => this.getDateRangeOptions("parkingEventData")}>
                    <Button>
                        {parkingEventData.filter.selectedCustomDateRange ? parkingEventData.filter.selectedCustomDateRange : "Select"}
                        <Icon
                            type="down"/>
                    </Button>
                </Dropdown>
                &nbsp;<Dropdown overlay={() => this.getXAxisOptions("parkingEventData")}>
                    <Button>
                        {parkingEventData.filter.selectedXAxisOption ? parkingEventData.filter.selectedXAxisOption : "Select"}
                        <Icon
                            type="down"/>
                    </Button>
                </Dropdown>
            </div>}>
                {loading.parkingEventData ? <Skeleton active/> :
                    <Line data={parkingEventData.chartData} options={parkingEventsChartOptions}/>}

            </Card>

            <Modal
                onCancel={this.handleCancel}
                title="Custom Date Range"
                visible={this.state.isOpencustomDateRangeModal ? true : false}
                footer={[]}

            >
                <RangePicker
                    onChange={(changedDateRange) => this.selectDateRange("Custom", changedDateRange)}/>
            </Modal>
        </div>)
    }
}
