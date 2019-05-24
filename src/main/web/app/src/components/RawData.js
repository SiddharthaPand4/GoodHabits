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
        setInterval(()=> this.refRawDataTable.fireFetchData(), 120000)
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
            Header: 'VID',
            accessor: 'vehicleId',
            id: 'VID'
        }, {
            Header: 'Direction',
            accessor: 'tag',
            id: 'tag'
        },
            {
                Header: 'Video',
                accessor: 'vid',
                id: 'video',
                Cell: e => e.value !== 0 ? <div style={{cursor:'pointer'}} onClick={() => this.showVideo(e)}>View</div> : <div>NA</div>
        },
            /*{
                Header: 'Screenshot',
                accessor: 'id',
                id: 'ss',
                Cell: e => e.original.vid !== 0 ? <div style={{cursor:'pointer'}} onClick={() => this.showScreenshot(e)}>View</div> : <div>NA</div>
            },*/
            {
                Header: 'Download',
                accessor: 'vid',
                id: 'dlvideo',
                Cell: e => e.original.vid !== 0 ? <div style={{cursor:'pointer'}} onClick={() => this.downloadVideo(e)}>Download</div> : <div>NA</div>
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

                    <div style={{cursor:'pointer'}} onClick={()=>this.downloadCsv()}> Download Data</div>
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

        const seek = e.original.timeStamp - e.original.vts - e.original.offset - 5;
        console.log("et", e.original.timeStamp);
        console.log("vts", e.original.vts);
        console.log("offset", e.original.offset);
        console.log("seek to", seek);
        console.log(e);
        //seek(time)
        this.setState({
            seek : seek,
            video:'/video/' + e.original.vid + "?" + Math.random()
        });
    }

    showScreenshot(e) {
        this.setState({
            ss:'/screenshot/' + e.original.id + "?" + Math.random()
        });
    }

    downloadVideo(e) {
        fetch('/video/' + e.original.vid)
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
        fetch('/csv/')
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