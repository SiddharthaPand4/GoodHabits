import React, {Component} from "react";
import {Button, Card, DatePicker, Dropdown, Icon, Menu, message, Modal} from "antd";
import DashboardService from "../services/DashboardService";
import CommonService from "../services/CommonService";
import Moment from 'moment';
import {Line} from 'react-chartjs-2';
import FeedService from "../services/FeedService";

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
                    toDate: {},
                    feedID:0,
                    location:'All'
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
                    toDate: {},
                    feedID:0,
                    location:'All'
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            }, incidentCount: {
                filter: {
                    selectedCustomDateRange: "Today",
                    selectedXAxisOption: "Hourly",
                    fromDate: {},
                    toDate: {},
                    feedID:0,
                    location:'All'
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            },
            anpr: {
                filter: {
                    selectedCustomDateRange: "Today",
                    selectedXAxisOption: "Hourly",
                    fromDate: {},
                    toDate: {},
                    feedID:0,
                    location:'All'
                },
                chartData: {
                    labels: [],
                    datasets: []
                }
            },
            isAnprAllowed: false,
            isAtccAllowed: false,
            isIncidentAllowed: false,
            isIncidentCountAllowed: false,
            feedOptions:[],
        };

        this.getIncidentVehicleCount = this.getIncidentVehicleCount.bind(this);
        this.getAtccVehicleCount = this.getAtccVehicleCount.bind(this);
        this.getAnprVehicleCount = this.getAnprVehicleCount.bind(this);
        this.getBarChartOptions = this.getBarChartOptions.bind(this);
        this.selectDateRange = this.selectDateRange.bind(this);
        this.selectXAxisOption = this.selectXAxisOption.bind(this);
        this.refresh = this.refresh.bind(this);
        this.getDateRangeOptions = this.getDateRangeOptions.bind(this);
        this.getXAxisOptions = this.getXAxisOptions.bind(this);
        this.handleDateRangeChange = this.handleDateRangeChange.bind(this);
        this.getFeeds = this.getFeeds.bind(this);


    }

    componentDidMount() {
        this.refresh();
        this.getFeeds();
    }

    showCustomDateRangeModal(graphName) {
        this.setState({
            isOpencustomDateRangeModal: graphName,
        });
    };

    handleCancel = () => {
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
        this.setState({graph, isOpencustomDateRangeModal}, () => {
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

        this.getAtccVehicleCount(this.state.atcc.filter.fromDate, this.state.atcc.filter.toDate, this.state.atcc.filter.selectedXAxisOption,this.state.atcc.filter.feedID);
        this.getAnprVehicleCount(this.state.anpr.filter.fromDate, this.state.anpr.filter.toDate, this.state.anpr.filter.selectedXAxisOption,this.state.anpr.filter.feedID);
        this.getIncidentCount(this.state.incidentCount.filter.fromDate, this.state.incidentCount.filter.toDate, this.state.incidentCount.filter.selectedXAxisOption,this.state.incidentCount.filter.feedID)
        this.getIncidentVehicleCount(this.state.incident.filter.fromDate, this.state.incident.filter.toDate, this.state.incident.filter.selectedXAxisOption,this.state.incident.filter.feedID);
    }

    getFeeds() {
        FeedService.getFeeds().then(response => {
            this.setState({feedOptions: response.data});
        }).catch(error => {
            console.log(error);
        })
    }

    getAtccVehicleCount(from_date, to_date, xAxis,feedID) {
        let {atcc} = this.state;
        atcc.chartData = {
            labels: [],
            datasets: []
        };
        DashboardService.getAtccVehicleCount(from_date, to_date, xAxis,feedID).then(resposne => {

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
                        borderColor: color,
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
            this.setState({atcc,isAtccAllowed: true});
        }).catch(error => {
            console.log(error);
        });
    }

    getAnprVehicleCount(from_date, to_date, xAxis,feedID) {
        let {anpr} = this.state;
        anpr.chartData = {
            labels: [],
            datasets: []
        };
        DashboardService.getAnprVehicleCount(from_date, to_date, xAxis,feedID).then(resposne => {

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
                anpr.chartData.labels = labelDates;
                let vehicleTypeIndex = 0;
                for (let vehicleType in rawDataByVehicleData) {

                    let color = DashboardService.getColor(vehicleTypeIndex);
                    let dataSet = {
                        label: vehicleType,
                        data: [],
                        borderColor: color,
                        backgroundColor: color
                    };

                    for (let i in labelDates) {
                        if (rawDataByVehicleData[vehicleType][labelDates[i]]) {
                            dataSet.data.push(rawDataByVehicleData[vehicleType][labelDates[i]].vehicleCount);
                        } else {
                            dataSet.data.push(0);
                        }
                    }
                    anpr.chartData.datasets.push(dataSet);
                    vehicleTypeIndex++;
                }
            }
            this.setState({anpr,isAnprAllowed: true});
        }).catch(error => {
            console.log(error);
        });
    }

    getIncidentCount = async (from_date, to_date, xAxis, feedID) => {
        let {incidentCount} = this.state
        incidentCount.chartData = {
            labels: [],
            datasets: []
        }

        DashboardService.getIncidentCountForAllTypes(from_date, to_date, xAxis,feedID).then(resposne => {

            let rawData = resposne.data;
            if (rawData && rawData.length > 0) {
                //let labelDates = DashboardService.enumerateDaysBetweenDates(from_date, to_date);
                let labelDates = [];

                let rawDataByIncidentData = [];
                for (let i in rawData) {

                    if (!labelDates.includes(rawData[i].date)) {
                        labelDates.push(rawData[i].date)
                    }

                    if (!rawDataByIncidentData[rawData[i].incidentType]) {
                        rawDataByIncidentData[rawData[i].incidentType] = {};
                    }
                    if (!rawDataByIncidentData[rawData[i].incidentType][rawData[i].date]) {
                        rawDataByIncidentData[rawData[i].incidentType][rawData[i].date] = rawData[i];
                    }
                }
                incidentCount.chartData.labels = labelDates;
                let incidentTypeIndex = 0;
                for (let incidentType in rawDataByIncidentData) {

                    let color = DashboardService.getColor(incidentTypeIndex);
                    let dataSet = {
                        label: incidentType,
                        data: [],
                        borderColor: color,
                        backgroundColor: color
                    };

                    for (let i in labelDates) {
                        if (rawDataByIncidentData[incidentType][labelDates[i]]) {
                            dataSet.data.push(rawDataByIncidentData[incidentType][labelDates[i]].incidentCount);
                        } else {
                            dataSet.data.push(0);
                        }
                    }
                    incidentCount.chartData.datasets.push(dataSet);
                    incidentTypeIndex++;
                }
            }
            this.setState({incidentCount, isIncidentCountAllowed: true});
        }).catch(error => {
            console.log(error);
        });

    }


    getIncidentVehicleCount(from_date, to_date, xAxis,feedID) {
        let {incident} = this.state;
        incident.chartData = {
            labels: [],
            datasets: []
        };
        DashboardService.getIncidentVehicleCount(from_date, to_date, xAxis,feedID).then(resposne => {

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
                borderColor: DashboardService.getColor(0),
                backgroundColor: DashboardService.getColor(0)
            };
            let reverseDirectionDataset = {
                label: "Reverse Direction",
                data: [],
                borderColor: DashboardService.getColor(2),
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
            this.setState({incident,isIncidentAllowed: true});
        }).catch(error => {
            this.setState({incident});
            console.log(error);
        });
    }

    handleDateRangeChange() {
        this.setState(() => {
            this.refresh();
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

    handleLocationMenuClick(graphName,choice) {
        let graph = this.state[graphName];

        if (choice.item.props.children == "All") {
            graph.filter.feedID = 0;
            graph.filter.location = 'All';
            this.setState({[graphName]: graph}, () => {
                this.refresh();
            });
        } else {
            graph.filter.feedID = choice.item.props.id;
            graph.filter.location = choice.item.props.children;
            this.setState({[graphName]: graph}, () => {
                this.refresh();
            });
        }
    }

    getLocation(graphName){
        return <Menu onClick={this.handleLocationMenuClick.bind(this,graphName)}>
            <Menu.Item key={'0'}>All</Menu.Item>
            {(this.state.feedOptions || []).map((feed) =>
                <Menu.Item key={feed.id} id={feed.id}>
                    {feed.site + ">" + feed.location}
                </Menu.Item>
            )}
        </Menu>
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
            },
            elements: {
                line: {
                    fill: false,
                }
            },
            plugins: {
                datalabels: {
                    display: false,
                    anchor: 'end',
                    clamp: true,
                    align: 'end',
                    offset: 6,
                    backgroundColor: function (context) {
                        return context.dataset.backgroundColor;
                    },
                    color: 'white',
                    font: {
                        weight: 'bold'
                    }
                }
            }


        };
        return options;
    }

    render() {
        let {atcc, incident, anpr, incidentCount} = this.state;
        const atccChartOptions = this.getBarChartOptions("atcc");
        const incidentChartOptions = this.getBarChartOptions("incident");
        const anprChartOptions = this.getBarChartOptions("anpr");
        const incidentCountChartOptions = this.getBarChartOptions("incidentCount");
        return (
            <div>
                <div>
                    <Modal
                        onCancel={this.handleCancel}
                        title="Custom Date Range"
                        visible={this.state.isOpencustomDateRangeModal}
                        footer={[]}

                    >
                        <RangePicker
                            onChange={(changedDateRange) => this.selectDateRange(this.state.isOpencustomDateRangeModal, "Custom", changedDateRange)}/>
                    </Modal>
                </div>
                <div>
                    {
                        this.state.isAnprAllowed
                            ?
                            <Card title={<div>ANPR
                                &nbsp;
                                <Dropdown overlay={() => this.getDateRangeOptions("anpr")}>
                                    <Button>
                                        {anpr.filter.selectedCustomDateRange ? anpr.filter.selectedCustomDateRange : "Select"}
                                        <Icon
                                            type="down"/>
                                    </Button>
                                </Dropdown>
                                &nbsp;<Dropdown overlay={() => this.getXAxisOptions("anpr")}>
                                    <Button>
                                        {anpr.filter.selectedXAxisOption ? anpr.filter.selectedXAxisOption : "Select"}
                                        <Icon
                                            type="down"/>
                                    </Button>
                                </Dropdown>
                                &nbsp;<Dropdown overlay={() =>this.getLocation('anpr')}>
                                <Button color="#f50">
                                    {anpr.filter.location ? anpr.filter.location : "All"}
                                    <Icon type="down"/>
                                </Button>
                            </Dropdown>
                            </div>}>
                                <Line data={anpr.chartData} options={anprChartOptions}/>
                            </Card>
                            :
                            null
                    }
                    <br/>
                    {
                        this.state.isAtccAllowed
                            ?
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
                                        {atcc.filter.selectedXAxisOption ? atcc.filter.selectedXAxisOption : "Select"}
                                        <Icon
                                            type="down"/>
                                    </Button>
                                </Dropdown>
                                &nbsp;<Dropdown overlay={() =>this.getLocation('atcc')}>
                                    <Button color="#f50">
                                        {atcc.filter.location ? atcc.filter.location : "All"}
                                        <Icon type="down"/>
                                    </Button>
                                </Dropdown>
                            </div>}>
                                <Line data={atcc.chartData} options={atccChartOptions}/>

                            </Card>
                            : null
                    }
                    <br/>
                    {
                        this.state.isIncidentCountAllowed
                            ?
                            <Card title={<div>Incident Counts
                                &nbsp;
                                <Dropdown overlay={() => this.getDateRangeOptions("incidentCount")}>
                                    <Button>
                                        {incidentCount.filter.selectedCustomDateRange ? incidentCount.filter.selectedCustomDateRange : "Select"}
                                        <Icon
                                            type="down"/>
                                    </Button>
                                </Dropdown>
                                &nbsp;<Dropdown overlay={() => this.getXAxisOptions("incidentCount")}>
                                    <Button>
                                        {incidentCount.filter.selectedXAxisOption ? incidentCount.filter.selectedXAxisOption : "Select"}
                                        <Icon
                                            type="down"/>
                                    </Button>
                                </Dropdown>
                                &nbsp;<Dropdown overlay={() =>this.getLocation('incidentCount')}>
                                    <Button color="#f50">
                                        {incidentCount.filter.location ? incidentCount.filter.location : "All"}
                                        <Icon type="down"/>
                                    </Button>
                                </Dropdown>
                            </div>}>
                                <Line data={incidentCount.chartData} options={incidentCountChartOptions}/>

                            </Card>
                            : null
                    }
                    <br/>
                    {
                        this.state.isIncidentAllowed
                            ?
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
                                &nbsp;<Dropdown overlay={() =>this.getLocation('incident')}>
                                    <Button color="#f50">
                                        {incident.filter.location ? incident.filter.location : "All"}
                                        <Icon type="down"/>
                                    </Button>
                                </Dropdown>
                            </div>}>
                                <Line data={incident.chartData} options={incidentChartOptions}/>
                            </Card>
                            : null}
                </div>
            </div>
        )
    }
}