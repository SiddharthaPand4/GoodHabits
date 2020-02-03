import React, {Component} from "react";
import {Line, Pie, Doughnut} from 'react-chartjs-2';
import {Card, Col, Row} from "antd";
import ApmsService from "../../services/ApmsService";
import {cloneDeep} from 'lodash';

let fo_data = {
    labels: [
        'Free',
        'Occupied',
    ],
    datasets: [{
        data: [0, 0],
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


let flow_data = {
    labels: ['8AM', '9AM', '10AM', '11AM', '12PM', '1PM', '2PM'],
    datasets: [{
        label: 'Flow',
        lineTension: 0.0,
        data: [65, 59, 80, 81, 56, 55, 40]
    }]
};

export default class ParkingDashboardView extends Component {


    constructor(props) {
        super(props);
        this.state = {
            loading: false,
            fo_data: fo_data,
            cb_data: {},
            entryexit_data: entryexit_data

        };

        this.refresh = this.refresh.bind(this);
        this.getParkingSlotStats = this.getParkingSlotStats.bind(this);

    }

    refresh() {
        this.getParkingSlotStats();
    }


    getParkingSlotStats() {
        let {fo_data} = this.state;
        ApmsService.getParkingSlotStats().then(response => {
            let statsData = response.data;
            fo_data.datasets[0].data[0] = statsData.freeSlots;
            fo_data.datasets[0].data[1] = statsData.totalSlots - statsData.freeSlots;

            let cb_data = {
                label: 'Car',
                labels: ['Parked Slots', 'Free Slots'],
                datasets: []
            };

            let dataset = {
                data: [statsData.carsParked, statsData.carSlots - statsData.carsParked],
                label: 'Car',
                labels: ["Parked Slots", "Free Slots"]
            };
            cb_data.datasets.push(cloneDeep(dataset));

            dataset = {
                label: 'Bike',
                data: [statsData.bikesParked, statsData.bikeSlots - statsData.bikesParked],
                labels: ["Parked Slots", "Free Slots"]
            };
            cb_data.datasets.push(cloneDeep(dataset));

            this.setState({fo_data, cb_data: cb_data});
        }).catch(error => {
            console.log(error);
        })
    }

    componentDidMount() {
        this.refresh();
    }


    render() {
        let {fo_data, cb_data} = this.state;

        return (<div>
            <Card><Row>
                <Col md={8}>
                    <Pie data={fo_data} options={{
                        title: {
                            display: true,
                            text: 'Free/Occupied'
                        }
                    }}/>
                </Col>
                <Col md={8}>
                    {cb_data ? <Doughnut data={cb_data} options={{
                        circumference: Math.PI,
                        rotation: Math.PI,
                        title: {
                            display: true,
                            text: ' Car/Bike'
                        },
                        tooltips: {
                            callbacks: {
                                title: function(item, data) {
                                    return data.datasets[item[0].datasetIndex].label;
                                },
                                label: function (item, data) {
                                    let label = data.datasets[item.datasetIndex].labels[item.index];
                                    let value = data.datasets[item.datasetIndex].data[item.index];
                                    return label + ': ' + value;
                                }
                            }
                        }
                    }}/> : null}


                </Col>
                <Col md={8}>
                    <Pie data={entryexit_data} options={{
                        title: {
                            display: true,
                            text: 'In Car/Bike'
                        }
                    }}/>
                </Col>
            </Row></Card>

            <Row>
                <Col md={24}>
                    <div style={{height: 100 + 'px'}}>
                        <Line data={flow_data}/>
                    </div>
                </Col>
            </Row>
        </div>)
    }
}