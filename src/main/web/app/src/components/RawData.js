import React, { Component } from 'react';
import 'react-table/react-table.css'

import ReactTable from 'react-table'
import {Row, Col} from "reactstrap";
export default class RawDataList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            data: null,
        };
    }

    componentDidMount() {
        fetch("/api/data/raw")
            .then(response => response.json())
            .then(data => this.setState({ data }));
    }

    render() {
        const data = this.state.data;

        if (!data) return (<div>Loading...</div>);


        const columns = [{
            Header: 'Id',
            accessor: 'id' // String-based value accessors!
        }, {
            Header: 'Type',
            accessor: 'type',
            Cell: props => <span className='number'>{props.value}</span> // Custom cell components!
        }, {
            id: 'Timestamp', // Required because our accessor is not a string
            Header: 'Timestamp',
            accessor: 'ts' // Custom value accessors!
        }, {
            Header: 'Lane',
            accessor: 'lane',
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
            <Col> <div>&nbsp;</div></Col>
        </Row>)
    }
}