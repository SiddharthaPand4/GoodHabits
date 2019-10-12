import React, {Component} from "react";
import {
    Card,
    Col,
    Collapse,
    Divider,
    Empty,
    Icon,
    Pagination,
    Row,
    Table,
    Tag,
} from 'antd';
import GenericFilter from "../components/GenericFilter";
import Moment from "react-moment";
import AnprService from "../services/AnprService";

const {Column} = Table;
const {Panel} = Collapse;


export default class DeviceView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            layout: "table",
            events: {},
        };

        this.refresh = this.refresh.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        AnprService.getEvents(this.state.filter).then(request => {
            this.setState({"events": request.data, loading: false})
        })
    }

    render() {

        let layout = this.state.layout;

        return (<div>ANPR
            <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                <Panel header="Filter" key="1">
                    <GenericFilter/>
                </Panel>
                <Panel header="Events" key="2">
                    {layout === "table" ? (this.renderGrid()) : (this.renderTable())}
                </Panel>
            </Collapse>
        </div>)
    }

    renderGrid() {

        if (this.state.loading || !this.state.incidents || this.state.incidents.Total === 0) {
            return <Empty description={false}/>
        }

        let incidents = this.state.incidents.Incidents;
        let count = this.state.incidents.Total;

        return <div style={{background: '#ECECEC', padding: '30px'}}>
            <Row>
                <Col>
                    <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange} showSizeChanger
                                defaultCurrent={1} total={count}/>
                </Col>
            </Row>

            <Row gutter={16}>
                {
                    incidents.map((incident, index) =>
                        <Col span={8} key={index}>
                            <Card
                                title={
                                    <div>
                                        <Tag color="#f50">{incident.event_type}</Tag>
                                        <Tag color="#2db7f5">{incident.event_date}</Tag>
                                        <Tag color="#87d068"><span><Moment format="LTS">{incident.event_start}</Moment></span><Icon
                                            type="right" hidden/><span hidden><Moment
                                            format="LTS">{incident.event_end}</Moment></span></Tag>
                                        <Tag color="#108ee9" hidden>{incident.event_duration}s</Tag>
                                    </div>
                                }
                                bordered={true}
                                cover={<img alt="incident"
                                            src={"/api/incident/image/" + incident.image_id + "/image.jpg"}/>}
                                actions={[
                                    <Icon type="right" key="play" onClick={() => this.showVideo(incident.video_id)}/>,
                                    <Icon type="edit" key="edit"/>,
                                    <Icon type="delete" key="delete" onClick={() => this.archiveIncident(incident)}/>,
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

        if (this.state.loading || !this.state.events || this.state.events.Total === 0) {
            return <Empty description={false}/>
        }

        let incidents = this.state.incidents.events;
        let count = this.state.incidents.Total;

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
                <Column title="ID" dataIndex="ID" key="ID"/>
                <Column title="Type" dataIndex="event_type" key="event_type"/>
                <Column title="Date" dataIndex="event_date" key="event_date"/>
                <Column title="Time" dataIndex="event_start" key="event_start"
                        render={event_start => (<Moment format="LTS">{event_start}</Moment>)}/>
                <Column title="Duration" dataIndex="event_duration" key="event_duration" render={dur => (<span>{dur}s</span>) }/>
                <Column title="Time" dataIndex="event_start" key="event_start" render={(text, incident) => (
                    <span>
                        <a onClick={() => this.showVideo(incident.video_id)}>Play</a>
                        <Divider type="vertical"/>
                        <a onClick={() => this.archiveIncident(incident)}>Delete</a>
                    </span>
                )}/>
            </Table>
        )
    }
}
