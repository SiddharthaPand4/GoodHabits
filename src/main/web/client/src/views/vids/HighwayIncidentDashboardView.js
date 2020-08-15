import React, {Component} from "react";
import {Doughnut, Pie} from 'react-chartjs-2';
import {Card, Col, Empty, Row, Tag,Collapse,Dropdown,Button, Menu,notification,Icon} from "antd";

import DashboardService from "../../services/DashboardService";
import 'chartjs-plugin-datalabels';
import VidsService from "../../services/VidsService";
import feedService from "../../services/FeedService";

const {Panel} = Collapse;

let flow_hourly_data = {
    labels: [],
    datasets: [{
        data: [],
        backgroundColor: DashboardService.getColors()
    }]
};

let flow_today_data = {
    labels: [],
    datasets: [{
        data: [],
        backgroundColor: DashboardService.getColors()
    }]
};

let incident_data = {
    labels: [],
    datasets: [{
        data: [],
        backgroundColor: DashboardService.getColors()
    }]
};

export default class HighwayIncidentDashboardView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            flow_hourly_data: flow_hourly_data,
            flow_today_data: flow_today_data,
            incident_data: incident_data,
            loaded: false,
            feedID: 0,
            feedOptions: [],
            location:'All'
        };

        this.refresh = this.refresh.bind(this);
        this.getFeeds = this.getFeeds.bind(this);
        this.handleLocationMenuClick = this.handleLocationMenuClick.bind(this);
    }

    componentDidMount() {
        this.intervalID = setInterval(this.refresh.bind(this), 5 * 1000);
        this.refresh();
        this.getFeeds();
    }

    componentWillUnmount() {
        clearInterval(this.intervalID);
    }

    getFeeds() {
        feedService.getFeeds().then(response => {
            this.setState({feedOptions: response.data});
        }).catch(error => {
            notification.open({
                message: 'Something went wrong ',
                discription: error
            });
        })
    }

    refresh() {
        var req={feedId:this.state.feedID};
        VidsService.getStats(req).then(response => {
            let onehrlabels = [];
            let onehrvalues = [];
            for (let i = 0; i < response.data.onehourstats.length; i++) {
                let pt = response.data.onehourstats[i];
                onehrlabels.push(pt.key);
                onehrvalues.push(pt.count);
            }

            flow_hourly_data.labels = onehrlabels;
            flow_hourly_data.datasets[0].data = onehrvalues;

            let dailylabels = [];
            let dailyvalues = [];
            for (let i = 0; i < response.data.todaystats.length; i++) {
                let pt = response.data.todaystats[i];
                dailylabels.push(pt.key);
                dailyvalues.push(pt.count);
            }

            flow_today_data.labels = dailylabels;
            flow_today_data.datasets[0].data = dailyvalues;

            let incilabels = [];
            let incivalues = [];

            for (let i = 0; i < response.data.incidents.length; i++) {
                let pt = response.data.incidents[i];
                incilabels.push(pt.key);
                incivalues.push(pt.count);
            }

            incident_data.labels = incilabels;
            incident_data.datasets[0].data = incivalues;

            this.setState({
                flow_today_data: flow_today_data,
                flow_hourly_data: flow_hourly_data,
                incident_data: incident_data,
                stats: response.data,
                loaded: true
            });
            this.setState({stats: response.data, loaded: true})
        })
    }

    handleLocationMenuClick(choice) {
        let feedID;
        if (choice.item.props.children == "All") {
            feedID = null;
            this.setState({feedID: feedID},()=>{this.refresh()})
            this.setState({location: "All"})
        } else {
            feedID = choice.item.props.id;
            this.setState({feedID: feedID},()=>{this.refresh()});
            this.setState({location: choice.item.props.children});


        }
    }

    render() {
        let {flow_hourly_data, flow_today_data, incident_data, loaded} = this.state;
        let stats = this.state.stats;

        const locationMenu = (

            <Menu onClick={this.handleLocationMenuClick}>
                <Menu.Item key={'0'}>All</Menu.Item>
                {(this.state.feedOptions || []).map((feed) =>
                    <Menu.Item key={feed.id} id={feed.id}>
                        {feed.site + ">" + feed.location}
                    </Menu.Item>
                )}
            </Menu>

        );

        if (!loaded) {
            return <Empty/>
        }

        return (
            <div>
                <Collapse bordered={false} defaultActiveKey={['1']}>
                    <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                    <Panel header="Filter" key="1">
                        Location <Dropdown overlay={locationMenu}>
                        <Button color="#f50">
                            {this.state.location}<Icon type="down"/>
                        </Button>
                    </Dropdown>

                    </Panel>
                </Collapse>
                <Row>
                    <Col xl={12} lg={12} md={12} sm={24} xs={24}>
                        <Row>
                            <Col>
                                <Card>
                                    <Pie data={flow_hourly_data} options={{
                                        title: {
                                            display: true,
                                            text: 'Traffic Flow (Last Hour)'
                                        },
                                        plugins: {
                                            datalabels: {
                                                display: true,
                                                color: '#fff',
                                                anchor: 'end',
                                                align: 'start',
                                                offset: -10,
                                                borderWidth: 2,
                                                borderColor: '#fff',
                                                borderRadius: 25,
                                                backgroundColor: (context) => {
                                                    return context.dataset.backgroundColor;
                                                },
                                                font: {
                                                    weight: 'bold',
                                                    size: '10'
                                                },
                                            }
                                        }
                                    }}/>
                                </Card>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <Card>
                                    <Doughnut data={flow_today_data} options={
                                        {
                                            circumference: Math.PI,
                                            rotation: Math.PI,
                                            title: {
                                                display: true,
                                                text: ' Traffic Flow (Today)'
                                            },
                                            plugins: {
                                                datalabels: {
                                                    display: true,
                                                    color: '#fff',
                                                    anchor: 'end',
                                                    align: 'start',
                                                    offset: -10,
                                                    borderWidth: 2,
                                                    borderColor: '#fff',
                                                    borderRadius: 25,
                                                    backgroundColor: (context) => {
                                                        return context.dataset.backgroundColor;
                                                    },
                                                    font: {
                                                        weight: 'bold',
                                                        size: '10'
                                                    },
                                                }
                                            }
                                        }
                                    }
                                    />
                                </Card>
                            </Col>
                        </Row>
                    </Col>
                    <Col xl={12} lg={12} md={12} sm={24} xs={24}>
                        <Row>
                            <Col>
                                <Card>
                                    <Pie data={incident_data} options={{
                                        title: {
                                            display: true,
                                            text: 'Traffic Flow (Last Hour)'
                                        },
                                        plugins: {
                                            datalabels: {
                                                display: true,
                                                color: '#fff',
                                                anchor: 'end',
                                                align: 'start',
                                                offset: -10,
                                                borderWidth: 2,
                                                borderColor: '#fff',
                                                borderRadius: 25,
                                                backgroundColor: (context) => {
                                                    return context.dataset.backgroundColor;
                                                },
                                                font: {
                                                    weight: 'bold',
                                                    size: '10'
                                                },
                                            }
                                        }
                                    }}/>
                                </Card>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <div>
                                    <Card>
                                        <Tag color="#f50">{stats.trafficState.density}</Tag><br/><br/>
                                        <img width={500} height={300} alt="Flow Image"
                                             src={"/public/vids/flowimage/" + stats.trafficState.id + "/image.jpg"}/>
                                    </Card>
                                </div>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </div>)
    }
}
