import React, {Component} from "react";
import {Col, Row} from "antd";
import 'react-table-6/react-table.css'
import "video-react/dist/video-react.css";
import ReactTable from 'react-table-6'
import {Player} from 'video-react';
import AtccService from "../../services/AtccService";
import Moment from "react-moment";

export default class AtccView extends Component {

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

        let filter = {
            page: page,
            pageSize: pageSize,
            sorted: sorted,
            filtered: filtered,
        };

        AtccService.getAtccData(filter).then(response => {
            handleRetrievedData(response.data);
        });


    }

    render() {
        const data = this.state.data;
        const pages = this.state.pages;
        const loading = this.state.loading;

        const columns = [{
            Header: 'Id',
            accessor: 'id',
            id: 'id',
            headerStyle: {textAlign: 'left'}
        }, {

            Header: 'Type',
            accessor: 'type',
            Cell: props => <span className='number'>{props.value}</span>, // Custom cell components!
            id: 'type',
            headerStyle: {textAlign: 'left'}
        }, {
            Header: 'DateTime',
            accessor: 'eventDate',
            id: 'eventDate',
            headerStyle: {textAlign: 'left'},
            Cell: props => <div><Moment format="ll">{props.value}</Moment>{' '}|{' '}<Moment
                format="LTS">{props.value}</Moment></div>
        }, {
            Header: 'Lane',
            accessor: 'lane',
            id: 'lane',
            headerStyle: {textAlign: 'left'}
        }, {
            Header: 'Direction',
            accessor: 'direction',
            id: 'direction',
            headerStyle: {textAlign: 'left'},
            Cell: props => <span>{props.value === 1 ? "fwd" : "rev"}</span>
        }, {
            Header: 'VID',
            accessor: 'vid',
            id: 'vid',
            headerStyle: {textAlign: 'left'}
        },
            {
                Header: 'Video',
                accessor: 'vid',
                id: 'video',
                headerStyle: {textAlign: 'left'},
                Cell: e => e.value !== 0 ?
                    <div style={{cursor: 'pointer'}} onClick={() => this.showVideo(e)}>View</div> : <div>NA</div>
            },
            {
                Header: 'Screenshot',
                accessor: 'id',
                id: 'ss',
                headerStyle: {textAlign: 'left'},
                Cell: e => e.original.vid !== 0 ?
                    <div style={{cursor: 'pointer'}} onClick={() => this.showScreenshot(e)}>View</div> : <div>NA</div>
            },
            {
                Header: 'Download',
                accessor: 'vid',
                id: 'dlvideo',
                headerStyle: {textAlign: 'left'},
                Cell: e => e.original.vid !== 0 ?
                    <div style={{cursor: 'pointer'}} onClick={() => this.downloadVideo(e)}>Download</div> :
                    <div>NA</div>
            }
        ];


        return (

            <Row>
                <Col>
                    <ReactTable
                        ref={(refRawDataTable) => {
                            this.refRawDataTable = refRawDataTable;
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
                            this.getRawData(state.page, state.pageSize, state.sorted, state.filtered, (res) => {

                                this.setState({
                                    data: res.data,
                                    pages: Math.ceil(res.totalElements / parseFloat(state.pageSize)),
                                    loading: false
                                })
                            });
                        }}
                    />

                    <div style={{cursor: 'pointer'}} onClick={() => this.downloadCsv()}> Download Data</div>
                </Col>
                <Col>
                    <div>
                        <Player
                            fluid={false}
                            width={500}
                            autoPlay
                            startTime={this.state.seek}
                            playsInline
                            poster="/synlabs-logo.png"
                            src={this.state.video}
                        />
                        <img src={this.state.ss || '/synlabs-logo.png'} alt="event screenshot"/>
                    </div>
                </Col>
            </Row>)
    }

    showVideo(e) {

        const seek = Math.max(e.original.seek - 5, 0);
        this.setState({
            seek: seek,
            video: '/public/atcc/video/' + e.original.id + "?r=" + Math.random()
        });
    }

    showScreenshot(e) {
        this.setState({
            ss: '/public/atcc/screenshot/' + e.original.id + "?r=" + Math.random()
        });
    }

    downloadVideo(e) {
        fetch('/api/atcc/video/' + e.original.vid)
            .then((response) => response.blob())
            .then((blob) => {
                const url = window.URL.createObjectURL(new Blob([blob]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', e.original.tag + "-" + e.original.vid + `-video.mp4`);
                document.body.appendChild(link);
                link.click();
                link.parentNode.removeChild(link);
            })
    }

    downloadCsv() {
        fetch('/api/atcc/csv/')
            .then((response) => response.blob())
            .then((blob) => {
                const url = window.URL.createObjectURL(new Blob([blob]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `data.csv`);
                document.body.appendChild(link);
                link.click();
                link.parentNode.removeChild(link);
            })
    }
}