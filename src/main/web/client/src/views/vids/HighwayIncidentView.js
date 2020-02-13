import React, {Component} from "react";
import {
    Card,
    Col,
    Collapse,
    Empty,
    Icon,
    Pagination,
    Row,
    Table,
    Tag,
    Input, Button, Menu, Dropdown, Typography
} from 'antd';
import GenericFilter from "../../components/GenericFilter";
import Moment from "react-moment";
import VidsService from "../../services/VidsService";

const {Text} = Typography;

const {Column} = Table;
const {Panel} = Collapse;


export default class HighwayIncidentView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            visible: true,
            loading: true,
            layout: "list",
            incidents: {},
            filter: {
                page: 1,
                pageSize: 12
            },
        };

        this.refresh = this.refresh.bind(this);
        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        VidsService.getIncidents(this.state.filter).then(request => {
            this.setState({"incidents": request.data, loading: false})
        })
    }

    //cant use refresh to read from state as state may not have been set
    refreshNow() {
        VidsService.getIncidents(this.state.filter).then(request => {
            this.setState({"incidents": request.data, loading: false})
        })
    }

    archiveEvent(event) {
        VidsService.archiveIncident(event).then(request => {
            this.refresh();
        })
    }

    handleFilterChange(data) {
        this.setState({filter: data})
    }

    handleLayoutChange(data) {
        this.setState({layout: data})
    }

    handleOnClick() {
        this.setState({
            visible: false,
        });
    };

    handleRefresh() {
        this.refresh();
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

    render() {

        let layout = this.state.layout;

        return (
            <div>
                <h3>Highway Incidents</h3>
                <Collapse bordered={false} defaultActiveKey={['1']}>
                    <Panel header="Filter" key="1">

                        <GenericFilter handleRefresh={this.refresh} filter={this.state.filter} layout={layout}
                                       handleFilterChange={this.handleFilterChange}
                                       handleLayoutChange={this.handleLayoutChange}/>
                    </Panel>
                </Collapse>
                <div>
                    {layout === "table" ? (this.renderTable()) : (this.renderGrid())}
                </div>
            </div>)
    }

    renderGrid() {


        if (this.state.loading || !this.state.incidents || this.state.incidents.totalPage === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.incidents.events;
        let count = this.state.incidents.totalPages * this.state.incidents.pageSize;


        return <div style={{background: '#ECECEC', padding: '5px'}}>
            <Row>
                {
                    events.map((event, index) =>
                        <Col xl={{span: 8}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}} key={index}>
                            <Card
                                style={{margin: "5px"}}
                                title={
                                    <div>
                                        <Tag color="#f50">{event.incidentType}</Tag>
                                        <div>
                                            <Text code><Icon type="schedule"/> <Moment
                                                format="ll">{event.eventDate}</Moment>{' '}|{' '}<Moment
                                                format="LTS">{event.eventDate}</Moment></Text>
                                        </div>
                                        <div style={{marginTop: "5px", textAlign: "left"}}>
                                            <div>
                                                <Text code><Icon type="environment"/> {event.location || "Location:NA"}</Text>
                                            </div>
                                        </div>

                                    </div>

                                }
                                extra={<Dropdown overlay={<Menu>
                                    <Menu.Item key="1">
                                        <a
                                            title={"click here to download"}
                                            href={"/public/vids/image/" + event.id + "/image.jpg"}
                                            download={true}><Icon type="download"/>{' '} Image</a>
                                    </Menu.Item>
                                    <Menu.Item key="2">
                                        <a
                                            title={"click here to download"}
                                            href={"/public/vids/video/" + event.id + "/image.mp4"}
                                            download={true}><Icon type="download"/>{' '} Video</a>
                                    </Menu.Item>
                                    <Menu.Item key="3">
                                        <Button type="danger" onClick={() => this.archiveEvent(event)}><Icon
                                            type="warning"/>{' '}
                                            Archive
                                        </Button>
                                    </Menu.Item>

                                </Menu>}>
                                    <Button>
                                        More <Icon type="down"/>
                                    </Button>
                                </Dropdown>}
                                bordered={true}
                                cover={<img alt="event" src={"/public/vids/image/" + event.id + "/image.jpg"}/>}
                            >


                            </Card>
                        </Col>
                    )
                }
            </Row>
            <div style={{textAlign: "right"}}>
                <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange} showSizeChanger
                            showQuickJumper
                            defaultCurrent={1} total={count} current={this.state.filter.page}
                            pageSize={this.state.filter.pageSize}/>
            </div>

        </div>
    }

    renderTable() {

        if (this.state.loading || !this.state.events || this.state.events.Total === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.anprresponse.events;
        let count = this.state.anprresponse.totalPages * this.state.anprresponse.pageSize;

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
            <Table dataSource={events} pagination={pagination}>
                <Column title="Location" dataIndex="location" key="location"
                        render={location => location}/>
                <Column title="Date" dataIndex="eventDate" key="eventDate"
                        render={eventDate => (<Moment format="L">{eventDate}</Moment>)}/>
                <Column title="Time" dataIndex="eventDate" key="eventTime"
                        render={eventDate => (<Moment format="LTS">{eventDate}</Moment>)}/>
                <Column title="LPR" dataIndex="anprText" key="anprText"
                        render={anprText => anprText}/>
                <Column title="LP Image" dataIndex="id" key="anprimage"
                        render={id => (
                            <a title={"click here to download"} href={"/public/anpr/lpr/" + id + "/image.jpg"}
                               download={true}>
                                <img alt="event"
                                     src={"/public/anpr/lpr/" + id + "/image.jpg"}/></a> )}/>
                <Column title="direction" dataIndex="direction" key="direction"
                        render={direction => direction}/>
                <Column title="Helmet?" dataIndex="helmet" key="helmet"
                        render={helmet => helmet ? <span>No</span> : <span>N/A</span>}/>
                <Column title="Speed" dataIndex="speed" key="speed"
                        render={speed => <span>{speed}</span>}/>
                <Column title="Action"
                        key="action"
                        render={(text, event) => (
                            <Button type="danger" onClick={() => this.archiveEvent(event)}><Icon type="warning"/>{' '}
                                Archive</Button>
                        )}
                />

            </Table>
        )
    }
}
