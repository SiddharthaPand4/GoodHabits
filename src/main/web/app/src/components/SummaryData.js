import React, { Component } from 'react';
import 'react-table/react-table.css'

import ReactTable from 'react-table'
import {Row, Col} from "reactstrap";
import {Bar} from "react-chartjs-2";
export default class SummaryDataList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            data: null,
        };

        this.makeChartData = this.makeChartData.bind(this)
    }

    componentDidMount() {
        fetch("/api/data/summary")
            .then(response => response.json())
            .then(data =>
                this.makeChartData(data)
            );
    }

    makeChartData(data) {

        var chartdata = { datasets:[], labels:[] };

        for (var i=0;i<data.length;i++) {
            chartdata.datasets.push(data[i].count);
            chartdata.labels.push(i);
        }

        this.setState({ data : data, chartdata: chartdata})
    }

    render() {
        const data = this.state.data;
        const chartdata = this.state.chartdata;
        if (!data) return (<div>Loading...</div>);

        const columns = [{
            Header: 'Span',
            accessor: 'ts'
        }, {
            Header: 'Type',
            accessor: 'type',
            Cell: props => <span className='number'>{props.value}</span> // Custom cell components!
        }, {
            id: 'Count', // Required because our accessor is not a string
            Header: 'Count',
            accessor: 'count' // Custom value accessors!
        }];

        return (


        <Row>
            <Col>
                <ReactTable
                    defaultPageSize={10}
                    data={data}
                    columns={columns}
                />

            </Col>
            <Col>
                <Bar
                    data={chartdata}
                    width={100}
                    height={50}
                    options={{ maintainAspectRatio: false }}
                />
            </Col>
        </Row>)
    }
}