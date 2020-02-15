    import React, {Component} from "react";
import {Doughnut, Pie} from 'react-chartjs-2';
import {Card, Col, Row, Skeleton} from "antd";

import DashboardService from "../../services/DashboardService";

import 'chartjs-plugin-datalabels';

let flow_hourly_data = {
    labels: [
        'Bike',
        'Car',
        'LCV',
        'Heavy'
    ],
    datasets: [{
        data: [],
        backgroundColor: [DashboardService.getColor(0), DashboardService.getColor(2)]
    }]
};

let flow_today_data = {
    labels: [
        'Bike',
        'Car',
        'LCV',
        'Heavy'
    ],
    datasets: [{
        data: [],
        backgroundColor: [DashboardService.getColor(6), DashboardService.getColor(1)]
    }]
};


let incident_data = {
    labels: [
        'Incidents',
    ],
    datasets: [{
        data: [],
        backgroundColor: [DashboardService.getColor(0), DashboardService.getColor(2)]
    }]
};

export default class HighwayIncidentDashboardView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            loading: {
                stats: false,
            },
            flow_hourly_data: flow_hourly_data,
            flow_today_data: flow_today_data,
            incident_data: incident_data
        };

        this.refresh = this.refresh.bind(this);
        this.refresh = this.refresh.bind(this);

    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {

    }

    render() {
        let {flow_hourly_data, flow_today_data, incident_data, loading} = this.state;

        return (<div>

            <Row>
                <Col xl={{span: 8}} lg={{span: 8}} md={{span: 8}} sm={{span: 24}} xs={{span: 24}}>
                    <Card>
                        {
                            loading.stats
                                ? <Skeleton active/>
                                : <Pie data={flow_hourly_data} options={{
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
                                            formatter: (item, context) => {
                                                return item + " Slots";
                                            }
                                        }
                                    }
                                }}/>
                        }
                    </Card>
                </Col>
                <Col xl={{span: 8}} lg={{span: 8}} md={{span: 8}} sm={{span: 24}} xs={{span: 24}}>
                    <Card>
                        {
                            loading.stats
                                ? <Skeleton active/>
                                : <Doughnut data={flow_today_data} options={
                                    {
                                        circumference: Math.PI,
                                        rotation: Math.PI,
                                        title: {
                                            display: true,
                                            text: ' Traffic Flow (Today)'
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
                                                formatter: (item, context) => {
                                                    return context.dataset.label + ": " + item;
                                                }
                                            }
                                        }
                                    }
                                }
                                />
                        }
                    </Card>


                </Col>
                <Col xl={{span: 8}} lg={{span: 8}} md={{span: 8}} sm={{span: 24}} xs={{span: 24}}>
                    <Card>
                        {
                            loading.stats
                                ? <Skeleton active/>
                                : <Pie data={incident_data} options={{
                                    title: {
                                        display: true,
                                        text: 'Traffic Status'
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
                                            formatter: (item, context) => {
                                                return item + " Slots";
                                            }
                                        }
                                    }
                                }}/>
                        }
                    </Card>
                </Col>
            </Row>
        </div>)
    }
}
