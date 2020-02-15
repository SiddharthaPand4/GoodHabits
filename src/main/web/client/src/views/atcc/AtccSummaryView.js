import React, {Component} from 'react';
import 'react-table-6/react-table.css'
import ReactTable from 'react-table-6'
import {Bar} from "react-chartjs-2";
import * as name from "chartjs-plugin-colorschemes";
import {Button, Col, Row} from "antd";
import AtccService from "../../services/AtccService";
const ButtonGroup = Button.Group;

export default class AtccSummaryView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            data: [],
            chartdata: null,
            loading: true,
            pages: 0,
            interval: "hour"
        };


        this.makeChartData = this.makeChartData.bind(this);
        this.getSummaryData = this.getSummaryData.bind(this);
        this.changeInterval = this.changeInterval.bind(this);
        //setInterval(()=> this.changeInterval(), 120000)
    }


    async changeInterval(value) {
        await this.setState({interval: value});
        this.refReactTable.fireFetchData();
    }

    getSummaryData(page, pageSize, sorted, filtered, handleRetrievedData) {

        this.setState({
            loading: true
        });

        let filter = {
            page: page,
            pageSize: pageSize,
            sorted: sorted,
            filtered: filtered,
        };

        let interval = this.state.interval;
        AtccService.getAtccSummaryData(filter, interval).then(response => {
            this.makeChartData(response.data);
            this.setState({
                loading: false
            });

            if (handleRetrievedData) {
                return handleRetrievedData(response.data);
            } else {
                return response;
            }
        });

    }

    makeChartData(data) {
        let chartdata = {
            labels: [],
            datasets: []
        };


        let timeSeries = new Set();
        for (let i = 0; i < data.length; i++) {

            if (data[i].span !== "Month") {
                timeSeries.add(data[i].date + " " + data[i].from);
            } else {
                timeSeries.add(data[i].from + " - " + data[i].to);
            }

            let index = -1;
            for (let j in chartdata.datasets) {
                if (chartdata.datasets[j].label) {
                    if (chartdata.datasets[j].label === data[i].type) {
                        index = j;
                        break;
                    }
                }
            }

            if (index === -1) {
                chartdata.datasets.push({
                    label: data[i].type,
                    data: [data[i].count]
                });
            } else {
                chartdata.datasets[index].data.push(data[i].count);
            }
        }

        chartdata.labels = Array.from(timeSeries);
        this.setState({chartdata: chartdata})
    }

    render() {
        const options = {
            scales: {
                xAxes: [{
                    stacked: true,
                    gridLines: {display: false},
                }],
                yAxes: [{
                    stacked: true,
                    ticks: {
                        beginAtZero: true
                    },
                }],
            },
            legend: {display: true},
            plugins: {
                colorschemes: {
                    scheme: 'brewer.Paired12'
                }
            }
        };

        const chartdata = this.state.chartdata;
        const data = this.state.data;
        const pages = this.state.pages;
        const loading = this.state.loading;
        const chartComponent = this.state.loading ? (<div>Loading...</div>) : (
            <Bar data={chartdata} options={options}/>);
        const columns = [{
            Header: 'Date',
            accessor: 'date',
            id: 'date'
        }, {
            Header: 'From',
            accessor: 'from',
            id: 'from'
        }, {
            Header: 'To',
            accessor: 'to',
            id: 'to'
        }, {
            Header: 'Span',
            accessor: 'span',
            id: 'span'
        }, {
            Header: 'Type',
            accessor: 'type',
            id: 'type',
            Cell: props => <span className='number'>{props.value}</span> // Custom cell components!
        }, {
            Header: 'Count',
            accessor: 'count', // Custom value accessors!
            id: 'count', // Required because our accessor is not a string
        }
        ];


        return (

            <Row>
                <Col>
                    <ButtonGroup>
                        <Button onClick={() => this.changeInterval('hour')}>Hour</Button>
                        <Button onClick={() => this.changeInterval('day')}>Day</Button>
                        <Button onClick={() => this.changeInterval('month')}>Month</Button>
                    </ButtonGroup>

                    <ReactTable
                        ref={(refReactTable) => {
                            this.refReactTable = refReactTable;
                        }}
                        defaultPageSize={10}
                        data={data}
                        columns={columns}
                        pages={pages}
                        className="-striped -highlight"
                        loading={loading}
                        showPagination={true}
                        showPaginationTop={false}
                        showPaginationBottom={true}
                        pageSizeOptions={[5, 10, 20, 25, 50, 100]}
                        manual // this would indicate that server side pagination has been enabled
                        onFetchData={(state, instance) => {
                            this.setState({loading: true});
                            this.getSummaryData(state.page, state.pageSize, state.sorted, state.filtered, (res) => {

                                this.setState({
                                    data: res.data,
                                    pages: Math.ceil(res.totalElements / parseFloat(state.pageSize)),
                                    loading: false
                                })
                            });
                        }}
                    />

                    <div style={{cursor:'pointer'}} onClick={()=>this.downloadCsv()}> Download Data</div>
                </Col>
                <Col>
                    <br/>
                    <br/>
                    <br/>
                    {chartComponent}
                </Col>
            </Row>)
    }

    downloadCsv() {

        var interval = this.state.interval;
        fetch('/csv/summary/' + interval)
            .then((response) => response.blob())
            .then((blob) => {
                const url = window.URL.createObjectURL(new Blob([blob]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', interval + `-data.csv`);
                document.body.appendChild(link);
                link.click();
                link.parentNode.removeChild(link);
            })
    }
}