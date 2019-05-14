import React, {Component} from 'react';
import 'react-table/react-table.css'

import ReactTable from 'react-table'
import {Row, Col} from "reactstrap";
import {Bar} from "react-chartjs-2";

export default class SummaryDataList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            data: [],
            chartdata: null,
            loading: true,
            pages:0
        };

        this.makeChartData = this.makeChartData.bind(this)
        this.getSummaryData = this.getSummaryData.bind(this)
    }

    componentDidMount() {

    }

    getSummaryData(page, pageSize, sorted, filtered, handleRetrievedData) {
        if(!page) page = 0;

        let url = "/api/data/summary";
        let postObject = {
            page: page,
            pageSize: pageSize,
            sorted: sorted,
            filtered: filtered,
        };
        this.setState({
            loading: true
        });
        fetch(url,{
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "PUT",
            body: JSON.stringify(postObject)
        })
            .then(response => response.json())
            .then(response => {
                    // this.makeChartData(data);
                    // this.setState({data: response.data,loading: false});
                    return handleRetrievedData(response);
                }
            );

    }

    makeChartData(data) {

        let chartdata = {
            labels: [],
            datasets: [{
                label: "Data",
                data: []
            }]
        };

        for (let i = 0; i < data.length; i++) {
            chartdata.datasets[0].data.push(data[i].count);
            chartdata.labels.push(data[i].type);
        }

        this.setState({chartdata: chartdata})
    }

    render() {

        const chartdata = this.state.chartdata;
        // if (!this.state.data) return (<div>Loading...</div>);

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
                        data={this.state.data}
                        columns={columns}
                        pages={this.state.pages}
                        className="-striped -highlight"
                        loading={this.state.loading}
                        showPagination={true}
                        showPaginationTop={false}
                        showPaginationBottom={true}
                        pageSizeOptions={[5, 10, 20, 25, 50, 100]}
                        manual // this would indicate that server side pagination has been enabled
                        onFetchData={(state, instance) => {
                            this.setState({loading: true});
                            this.getSummaryData(state.page, state.pageSize, state.sorted, state.filtered, (res) => {
                                console.log(res);
                                this.setState({
                                    data: res.data,
                                    pages: Math.ceil(res.totalElements / parseFloat(state.pageSize)),
                                    loading: false
                                })
                            });
                        }}
                    />
                </Col>
                <Col>
                    <br/>
                    <br/>
                    <br/>
                    {/*<Bar*/}
                        {/*data={chartdata}*/}
                    {/*/>*/}
                </Col>
            </Row>)
    }
}