import React, {Component} from "react";
import {Line, Pie} from 'react-chartjs-2';
import {Col, Row} from "antd";

const fo_data = {
    labels: [
        'Free',
        'Occupied',
    ],
    datasets: [{
        data: [15, 5],
        backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ],
        hoverBackgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ]
    }]
};

const cb_data = {
    labels: [
        'Car',
        'Bike',
    ],
    datasets: [{
        data: [20, 0],
        backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ],
        hoverBackgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ]
    }]
};

const entryexit_data = {
    labels: [
        'Car - In',
        'Bike - In',
    ],
    datasets: [{
        data: [44, 0],
        backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ],
        hoverBackgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
        ]
    }]
};

const flow_data = {
    labels: ['8AM', '9AM', '10AM', '11AM', '12PM', '1PM', '2PM'],
    datasets: [{
        label: 'Flow',
        lineTension: 0.0,
        data: [65, 59, 80, 81, 56, 55, 40]
    }]
};

export default class ParkingDashboardView extends Component {

    render() {
        return (<div>
            <Row>
                <Col md={8}>
                    <Pie data={fo_data}/>
                    <label>Free/Occupied</label>
                </Col>
                <Col md={8}>
                    <Pie data={cb_data}/>
                    <label>Car/Bike</label>
                </Col>
                <Col md={8}>
                    <Pie data={entryexit_data}/>
                    <label>In Car/Bike</label>
                </Col>
            </Row>
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