import React, {Component} from 'react';
import 'react-table/react-table.css'
import "video-react/dist/video-react.css";
import ReactTable from 'react-table'
import {Row, Col} from "reactstrap";
import { Player } from 'video-react';
export default class RawDataList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            data: [],
            loading: true,
            pages: 0,
            video: null
        };
        this.getRawData = this.getRawData.bind(this);
    }

    getRawData(page, pageSize, sorted, filtered, handleRetrievedData) {

        this.setState({
            loading: true
        });

        let requestBody = {
            page: page,
            pageSize: pageSize,
            sorted: sorted,
            filtered: filtered,
        };

        fetch("/api/data/raw", {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "PUT",
            body: JSON.stringify(requestBody)
        }).then(response => response.json())
            .then(response => {

                    return handleRetrievedData(response);
                }
            );

    }

    render() {
        const data = this.state.data;
        const pages = this.state.pages;
        const loading = this.state.loading;

        const columns = [{
            Header: 'Id',
            accessor: 'id',
            id: 'id'
        }, {

            Header: 'Type',
            accessor: 'type',
            Cell: props => <span className='number'>{props.value}</span>, // Custom cell components!
            id: 'type'
        }, {
            Header: 'Date',
            accessor: 'date',
            id: 'date'
        }, {
            Header: 'Time',
            accessor: 'time',
            id: 'time'
        }, {
            Header: 'Lane',
            accessor: 'lane',
            id: 'lane'
        }, {
            Header: 'Speed',
            accessor: 'speed',
            id: 'speed'
        }, {
            Header: 'Direction',
            accessor: 'tag',
            id: 'tag'
        },
            {
                Header: 'Video',
                accessor: 'vid',
                id: 'video',
                Cell: e => e.value !== 0 ? <div style={{cursor:'pointer'}} onClick={() => this.showVideo(e)}>Click</div> : <div>NA</div>
        },
            {
                Header: 'Screenshot',
                accessor: 'id',
                id: 'ss',
                Cell: e => e.original.vid !== 0 ? <div style={{cursor:'pointer'}} onClick={() => this.showScreenshot(e)}>Click</div> : <div>NA</div>
            }
        ];


        return (

            <Row>
                <Col>
                    <ReactTable
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
                            this.getRawData(state.page, state.pageSize, state.sorted, state.filtered, (res) => {

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
                    <div>
                        <Player
                            startTime={this.state.seek}
                            autoPlay
                            playsInline
                            poster="/logo.png"
                            src={this.state.video}
                        />
                        <img src={this.state.ss || '/logo.png'} alt="event screenshot"/>
                    </div>
                </Col>
            </Row>)
    }

    showVideo(e) {

        //seek(time)
        this.setState({
            seek : e.original.timeStamp - e.original.vid,
            video:'/video/' + e.original.vid + "?" + Math.random()
        });
    }

    showScreenshot(e) {
        this.setState({
            ss:'/screenshot/' + e.original.id + "?" + Math.random()
        });
    }
}