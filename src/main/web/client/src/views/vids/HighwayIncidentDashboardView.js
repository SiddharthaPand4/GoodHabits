import React, {Component} from "react";
import {Doughnut, Pie} from 'react-chartjs-2';
import {Card, Col, Empty, Row, Tag} from "antd";

import DashboardService from "../../services/DashboardService";

import 'chartjs-plugin-datalabels';
import VidsService from "../../services/VidsService";

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
            loaded: false,
            flow_hourly_data: flow_hourly_data,
            flow_today_data: flow_today_data,
            incident_data: incident_data
        };

        this.refresh = this.refresh.bind(this);

    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        VidsService.getStats().then(response => {
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

            this.setState({
                flow_today_data: flow_today_data,
                flow_hourly_data: flow_hourly_data,
                stats: response.data,
                loaded: true
            });
            this.setState({stats: response.data, loaded: true})
        })
    }

    render() {
        let {flow_hourly_data, flow_today_data, loaded} = this.state;
        let stats = this.state.stats;

        if (!loaded) {
            return <Empty/>
        }

        return (
            <div>
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
