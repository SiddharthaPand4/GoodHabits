import React, {Component} from "react";
import {
    Button,
    Card,
    Col,
    Collapse,
    DatePicker,
    Divider,
    Empty,
    Icon,
    message,
    Modal,
    Pagination,
    Row,
    Table,
    Tag,
    TimePicker
} from 'antd';
import IncidentService from "../services/TrafficIncidentService";
import ReactPlayer from "react-player";
import Moment from 'react-moment';

const {Column, ColumnGroup} = Table;
const {Panel} = Collapse;
const ButtonGroup = Button.Group;
const {confirm} = Modal;


export default class IncidentListView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            videoVisible: false,
            layout: "list",
            incidents: {},
            filter: {
                page: 1,
                pageSize: 10
            }
        };
        this.handleDone = this.handleDone.bind(this);
        this.archiveIncident = this.archiveIncident.bind(this);
        this.refresh = this.refresh.bind(this);
        this.onFromDateChange = this.onFromDateChange.bind(this);
        this.onFromTimeChange = this.onFromTimeChange.bind(this);

        this.onToDateChange = this.onToDateChange.bind(this);
        this.onToTimeChange = this.onToTimeChange.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        let self = this;
        IncidentService.getIncidents(this.state.filter).then(response => {
            console.log("daada", response.data);
            self.setState({"incidents": response.data, loading: false})
        },
        error => {
            message.error(error.response.data.message);
        })
    }

    //cant use refresh to read from state as state may not have been set
    refreshNow(filter) {
        IncidentService.getIncidents(filter).then(request => {
            this.setState({"incidentsresponse": request.data, loading: false, filter: filter})
        })
    }

    archiveIncident(incident) {
        IncidentService.archiveIncident(incident).then(() => {
                this.refresh();
                message.success("Incident successfully archived!");
            },
            error => {
                message.error(error.response.data.message);
            })
    }

    changeLayout(layout) {
        this.setState({"layout": layout});
    }

    showVideo(videoid) {

        let cp = ReactPlayer.canPlay("/api/incident/video/" + this.state.videoid + "/video.mp4");
        this.setState({
            videoVisible: true,
            videoid: videoid
        });
    };

    handleDone() {
        this.setState({
            videoVisible: false,
            videoid: ""
        });
    };

    onFromDateChange(date) {
        let filter = this.state.filter;
        if (date != null) {
            filter.fromDate = date.format("YYYY-MM-DD");
        } else {
            filter.fromDate = null;
        }
        this.setState({filter: filter});
    }

    onFromTimeChange(time) {

        let filter = this.state.filter;
        if (time != null) {
            filter.fromTime = time.format("HH:mm:ss");
        } else {
            filter.fromTime = null;
        }
        this.setState({filter: filter});
    }

    onToDateChange(date) {
        let filter = this.state.filter;
        if (date != null) {
            filter.toDate = date.format("YYYY-MM-DD");
        } else {
            filter.toDate = null;
        }
        this.setState({filter: filter});
    }

    onToTimeChange(time) {
        let filter = this.state.filter;
        if (time != null) {
            filter.toTime = time.format("HH:mm:ss");
        } else {
            filter.toTime = null;
        }
        this.setState({filter: filter});
    }

    onPageChange(page, pageSize) {
        let filter = this.state.filter;
        filter.page = page;
        filter.pageSize = pageSize;
        this.refreshNow(filter)
    }

    onPageSizeChange(current, pageSize) {
        let filter = this.state.filter;
        filter.pageSize = pageSize;
        this.refreshNow(filter);
    }

    showDeleteConfirm(incident, refresh) {
        confirm({
            title: 'Are you sure you want to archive the incident ?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk() {
                console.log('OK');
                IncidentService.archiveIncident(incident).then(() => {
                        refresh();
                        message.success("Incident successfully archived!");
                    },
                    error => {
                        message.error(error.response.data.message);
                    })
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    render() {

        let layout = this.state.layout;

        return (

            <div>
                <Modal
                    title="Video"
                    visible={this.state.videoVisible}
                    onOk={this.handleDone}
                    onCancel={this.handleDone}
                >
                    {this.state.videoVisible ? (<ReactPlayer controls={true}
                                                             url={"/api/incident/video/" + this.state.videoid + "/video.mp4"}
                                                             width="300"/>) : (<div><Empty/></div>)}
                </Modal>

                <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                    <Panel header="Filter" key="1">
                        <span>From: </span>
                        <DatePicker onChange={this.onFromDateChange}/>&nbsp;&nbsp;
                        <TimePicker minuteStep={15} secondStep={60} onChange={this.onFromTimeChange}/>&nbsp;&nbsp;
                        <span>To: </span>
                        <DatePicker onChange={this.onToDateChange}/>&nbsp;&nbsp;
                        <TimePicker minuteStep={15} secondStep={60} onChange={this.onToTimeChange}/>&nbsp;&nbsp;

                        <ButtonGroup>
                            <Button type={layout === "list" ? "primary" : ""} size="small" icon="unordered-list"
                                    onClick={() => {
                                        this.changeLayout("list")
                                    }}/>
                            <Button type={layout === "table" ? "primary" : ""} size="small" icon="table"
                                    onClick={() => {
                                        this.changeLayout("table")
                                    }}/>
                        </ButtonGroup>&nbsp;&nbsp;
                        <Button onClick={() => {
                            this.refresh()
                        }}><Icon type="reload"/>Reload</Button>
                    </Panel>
                    <Panel header="Incidents" key="2">
                        {layout === "table" ? (this.renderGrid()) : (this.renderTable())}
                    </Panel>
                </Collapse>
            </div>
        )
    }

    renderGrid() {

        if (this.state.loading || !this.state.incidents || this.state.incidents.Total === 0) {
            return <Empty description={false}/>
        }

        let incidents = this.state.incidents.events;
        let count = this.state.incidents.totalPages * this.state.incidents.pageSize;


        return <div style={{background: '#ECECEC', padding: '5px'}}>
            <Row>
                <Col>
                    <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange} showSizeChanger
                                showQuickJumper
                                defaultCurrent={1} total={count} current={this.state.filter.page}
                                pageSize={this.state.filter.pageSize}/>
                </Col>
            </Row>

            <Row gutter={16}>
                {
                    incidents.map((incident, index) =>
                        <Col span={8} key={index}>
                            <Card
                                title={
                                    <div>
                                        <Tag color="#f50">{incident.eventType}</Tag>
                                        <Tag color="#2db7f5">{incident.eventDate}</Tag>
                                        <Tag color="#87d068"><span><Moment
                                            format="LTS">{incident.eventStart}</Moment></span><Icon
                                            type="right" hidden/><span hidden><Moment
                                            format="LTS">{incident.eventEnd}</Moment></span></Tag>
                                        <Tag color="#108ee9" hidden>{incident.eventDuration}s</Tag>
                                    </div>
                                }
                                bordered={true}
                                cover={<img alt="incident image"
                                            src={"/api/incident/image/" + incident.imageId + "/image.jpg"}/>}
                                actions={[
                                    <Icon type="right" key="play" onClick={() => this.showVideo(incident.videoId)}/>,
                                    <Icon type="edit" key="edit"/>,
                                    <Icon type="delete" key="delete"
                                          onClick={this.showDeleteConfirm.bind(this, incident, this.refresh)}/>,
                                ]}
                            >

                            </Card>
                        </Col>
                    )
                }
            </Row>
        </div>
    }

    renderTable() {

        if (this.state.loading || !this.state.incidents || this.state.incidents.Total === 0) {
            return <Empty description={false}/>
        }

        let incidents = this.state.incidents.events;
        let count = this.state.incidents.totalPages * this.state.incidents.pageSize;

        const paginationOptions = {
            showSizeChanger: true,
            showQuickJumper: true,
            onShowSizeChange: this.onPageSizeChange,
            onChange: this.onPageChange,
            total: count
        };

        const pagination = {
            ...paginationOptions,
            total: count,
            current: this.state.filter.page,
            pageSize: this.state.filter.pageSize
        };

        return (
            <Table dataSource={incidents} pagination={pagination}>
                <Column title="ID" dataIndex="id" key="id"/>
                <Column title="Type" dataIndex="eventType" key="eventType"/>
                <Column title="Date" dataIndex="eventDate" key="eventDate"/>
                <Column title="Time" dataIndex="eventStart" key="eventStart"
                        render={eventStart => (<Moment format="LTS">{eventStart}</Moment>)}/>
                <Column title="Duration" dataIndex="eventDuration" key="eventDuration"
                        render={dur => (<span>{dur}s</span>)}/>
                <Column title="Action" key="action" render={(text, incident) => (
                    <span>
                        <a onClick={() => this.showVideo(incident.videoId)}>Play</a>
                        <Divider type="vertical"/>
                        <a onClick={this.showDeleteConfirm.bind(this, incident, this.refresh)}>Archive</a>
                    </span>
                )}/>
            </Table>
        )
    }
}
