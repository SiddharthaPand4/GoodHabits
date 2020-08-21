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
    Input, Button, Menu, Dropdown, Typography, Slider,
    Modal,
    message, Form, Spin, Select
} from 'antd';
import GenericFilter from "../../components/GenericFilter";
import Moment from "react-moment";
import Magnifier from "react-magnifier";
import AtccService from "../../services/AtccService";
import Switch from "antd/es/switch";
import {Player} from "video-react";
import {saveAs} from 'file-saver';
import FeedService from "../../services/FeedService";

const {Paragraph, Text} = Typography;

const {Column} = Table;
const {Panel} = Collapse;
const {Option} = Select;


export default class AtccGridView extends Component {


    constructor(props) {
        super(props);
        this.state = {
            renderVid: false,
            feedsList: [],
            activePanelKey: ["1"],
            visible: true,
            loading: true,
            layout: "list",
            events: {},
            filter: {
                page: 1,
                pageSize: 12,
                feedId: 0,
            },
            workingEvent: [],
            workingEventLoading: false,
            magnifyEvent: {
                magnifyEventId: "",
                zoomFactor: 2,
                minZoomFactor: 1,
                maxZoomFactor: 5
            },
            downloading: false,
            pageSizeOptions: ['12', '24', '48', '100', "250", "500"]
        };

        this.refresh = this.refresh.bind(this);
        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.videoSwitchChange = this.videoSwitchChange.bind(this);
        this.editEvent = this.editEvent.bind(this);
        this.magnifyEvent = this.magnifyEvent.bind(this);
        this.updateZoomFactor = this.updateZoomFactor.bind(this);
        this.onCollapse = this.onCollapse.bind(this);

    }

    componentDidMount() {
        this.myInstant = setInterval(() => {
            this.refresh()
        }, 30000);
        this.fetchFeedsList();
        this.refresh();
    }

    componentWillUnmount() {
        clearInterval(this.myInstant);
    }

    fetchFeedsList = async () => {
        try {
            const res = await FeedService.getFeeds()
            const feedsList = res.data
            this.setState({feedsList})
        } catch (e) {
            console.log(e)
            message.error("Something Went Wrong")
        }
    }

    videoSwitchChange() {
        if (this.state.renderVid === false) {
            this.setState({renderVid: true});
            this.refresh()
        } else {
            this.setState({renderVid: false});
            this.refresh()
        }
    };

    refresh() {
        this.setState({loading: true});
        AtccService.getEvents(this.state.filter).then(response => {
            this.setState({"atccresponse": response.data, loading: false})
        }).catch(error => {
            alert("Something went wrong!");
            this.setState({loading: false});
        })
    }


    handleFilterChange(data) {
        this.setState({filter: data})
    }

    handleLayoutChange(data) {
        this.setState({layout: data})
    }

    handleOnClick = e => {
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
        this.setState({filter}, () => {
            this.refresh();
        });
    }

    onPageSizeChange(current, pageSize) {
        let filter = this.state.filter;
        filter.pageSize = pageSize;
        this.setState({filter}, () => {
            this.refresh();
        });
    }

    editEvent(event) {
        this.setState({workingEvent: event});
    }

    magnifyEvent(event) {
        let magnifyEvent = this.state.magnifyEvent;
        magnifyEvent.magnifyEventId = event.id;

        this.setState({magnifyEvent});
    }

    updateZoomFactor(zoomFactor) {
        let magnifyEvent = this.state.magnifyEvent;
        magnifyEvent.zoomFactor = zoomFactor;

        this.setState({magnifyEvent});
    }

    onCollapse(change) {
        this.setState({activePanelKey: change})
    }

    feedSelected = feedId => {
        const filter = {...this.state.filter, feedId}
        this.setState({filter})
    }

    render() {

        let layout = this.state.layout;
        let lpr = this.state.filter.lpr;
        let activePanelKey = this.state.activePanelKey;

        return (
            <div>
                <h3>ATCC</h3>


                <div>
                    <Collapse className={"no-print"} bordered={false} defaultActiveKey={['1']}
                              activeKey={activePanelKey}
                              onChange={(e) => this.onCollapse(e)}>

                        <Panel header="Filter" key="1">
                            <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            </span>Video <Switch onChange={this.videoSwitchChange}/>
                            <br/><br/>
                            <Select
                                style={{width: 200}}
                                placeholder="Select Location"
                                onChange={this.feedSelected}
                            >
                                {(this.state.feedsList || []).map(feed => <Option
                                    value={feed.id}>{feed.site + " > " + feed.location}</Option>)}
                            </Select>
                            <br/><br/>
                            <GenericFilter handleRefresh={this.refresh} filter={this.state.filter} layout={layout}
                                           handleFilterChange={this.handleFilterChange}
                                           handleLayoutChange={this.handleLayoutChange}
                            />

                        </Panel>
                    </Collapse>

                    <div>
                        <Spin spinning={this.state.loading}>
                            {layout === "table" ? (this.renderTable()) : (this.renderGrid())}
                        </Spin>
                    </div>
                </div>


            </div>)
    }


    renderGrid() {
        if (this.state.loading || !this.state.atccresponse || this.state.atccresponse.totalPage === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.atccresponse.events;
        let workingEventLoading = this.state.workingEventLoading;
        let workingEvent = this.state.workingEvent;
        let count = this.state.atccresponse.totalPages * this.state.atccresponse.pageSize;
        let renderVid = this.state.renderVid;
        let {magnifyEventId, zoomFactor, minZoomFactor, maxZoomFactor} = this.state.magnifyEvent;
        const mid = ((maxZoomFactor - minZoomFactor) / 2).toFixed(5);
        const preColor = zoomFactor >= mid ? '' : 'rgba(0, 0, 0, .45)';
        const nextColor = zoomFactor >= mid ? 'rgba(0, 0, 0, .45)' : '';
        const marks = {
            1: {label: <span><Icon style={{color: preColor}} type="zoom-out"/></span>},
            2: {label: <span>2</span>},
            3: {label: <span>3</span>},
            4: {label: <span>4</span>},
            5: {label: <span><Icon style={{color: nextColor}} type="zoom-in"/></span>,}
        };
        return <div style={{background: '#ECECEC', padding: '5px'}}>
            <Row>
                {
                    events.map((event, index) =>
                        <Col xl={{span: 8}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}} key={index}>
                            <Card
                                style={{margin: "5px"}}
                                title={
                                    <div>
                                        {(event.type) ? <Tag color="#f50">{event.type}</Tag> : null}
                                        <Tag color="#f50">{(event.direction === 1 ? "Fwd" : "Rev")}</Tag>
                                        <br/>
                                        {(event.lane) ? <Tag color="#f50">Lane : {event.lane}</Tag> : null}
                                        <Tag color="#f50">Speed : {event.speed}</Tag>

                                    </div>
                                }
                                extra={<Dropdown overlay={<Menu>
                                    <Menu.Item key="0" onClick={() => this.magnifyEvent(event)}><Icon type="zoom-in"/>Zoom
                                        image
                                    </Menu.Item>
                                    {
                                        (event.vehicleImage != "") ?
                                            <Menu.Item key="1">
                                                <a
                                                    title={"click here to download"}
                                                    onClick={() => this.downloadImage(event)}
                                                ><Icon type="download"/>{' '} Image</a>
                                            </Menu.Item>
                                            :
                                            null
                                    }
                                    {(event.eventVideo != "") ?
                                        <Menu.Item key="2">
                                            <a
                                                title={"click here to download"}
                                                onClick={() => this.downloadVideo(event)}
                                            ><Icon type="download"/>{' '} Video</a>
                                        </Menu.Item>
                                        : null
                                    }
                                </Menu>}>
                                    <Button>
                                        More <Icon type="down"/>
                                    </Button>
                                </Dropdown>}

                                bordered={true}
                                cover={renderVid === false ?
                                    (
                                        (magnifyEventId === event.id) ?
                                            <Magnifier src={"/public/atcc/screenshot/" + event.id}
                                                       zoomFactor={zoomFactor}/>
                                            :
                                            <img alt={"event"}
                                                 src={"/public/atcc/screenshot/" + event.id}/>
                                    )
                                    :
                                    (<Player
                                        playsInline
                                        poster={"/public/atcc/screenshot/" + event.id}
                                        src={"/public/atcc/video/" + event.id} alt={"No video"}
                                    />)
                                }

                            >
                                {renderVid === false ?
                                    (
                                        <div>
                                            {(magnifyEventId === event.id) ?
                                                <Slider
                                                    marks={marks}
                                                    min={minZoomFactor}
                                                    max={maxZoomFactor}
                                                    onChange={this.updateZoomFactor}
                                                    value={typeof zoomFactor === 'number' ? zoomFactor : 0}
                                                />
                                                :
                                                <div style={{height: "54px", textAlign: "center"}}>
                                                    <Button size="small" type="dashed"
                                                            onClick={() => this.magnifyEvent(event)}>
                                                        <Icon type="zoom-in"/>Zoom Image
                                                    </Button>
                                                </div>

                                            }
                                        </div>
                                    )
                                    : null}
                                <div style={{textAlign: "center"}}>
                                    <Text code><Icon type="schedule"/> <Moment
                                        format="ll">{event.eventDate}</Moment>{' '}|{' '}<Moment
                                        format="LTS">{event.eventDate}</Moment></Text>
                                </div>
                                {
                                    event.feed
                                    ? <div style={{textAlign: "center", marginTop:"2px"}}>
                                        <Text code>
                                            <Icon type="environment"/>
                                            {' '}
                                            {event.feed.location}{' '}|{' '}
                                            {event.feed.site}{' '}|{' '}
                                            {event.feed.name}
                                        </Text>
                                    </div>
                                    : null
                                }

                            </Card>
                        </Col>
                    )
                }
            </Row>
            <div style={{textAlign: "right"}}>
                <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange} showSizeChanger
                            showQuickJumper
                            defaultCurrent={1} total={count} current={this.state.filter.page}
                            pageSize={this.state.filter.pageSize} pageSizeOptions={this.state.pageSizeOptions}/>
            </div>

        </div>
    }

    renderTable() {

        if (this.state.loading || !this.state.events || this.state.events.Total === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.atccresponse.events;
        let count = this.state.atccresponse.totalPages * this.state.atccresponse.pageSize;

        const paginationOptions = {
            showSizeChanger: true,
            showQuickJumper: true,
            onShowSizeChange: this.onPageSizeChange,
            onChange: this.onPageChange,
            total: count,
            pageSizeOptions: this.state.pageSizeOptions
        };

        const pagination = {
            ...paginationOptions,
            total: count,
            current: this.state.filter.page,
            pageSize: this.state.filter.pageSize
        };

        return (
            <Table dataSource={events} pagination={pagination} size="small">
                <Column title="ID" dataIndex="id" key="id"/>
                <Column title="Type" dataIndex="type" key="type"/>
                <Column title="Date & Time" dataIndex="eventDate" format={"ll"} key="eventDate"
                        render={eventDate => (
                            <div>
                                <Moment format="ll">{eventDate}</Moment>
                                {' '}|{' '}
                                <Moment format="LTS">{eventDate}</Moment>
                            </div>)}/>
                <Column title="Speed" dataIndex="speed" key="speed"/>
                <Column title="Lane" dataIndex="lane" key="lane"/>
                <Column title="Direction" dataIndex="direction" key="direction"
                        render={direction => (<div>{direction === 1 ? "Fwd" : "Rev"}</div>)}/>
                <Column title="Location" dataIndex="location" key="location"/>
                <Column title="Image" dataIndex="id" key="id"
                        render={(id) => (
                            <Button type="primary" title={"click here to download"}
                                    href={"/public/atcc/screenshot/" + id}
                                    download={true}>
                                <Icon type="download"/>
                            </Button>)}/>

                <Column title="Video"
                        render={(event) => (
                            <Button type="primary" title={"Download vid"}
                                    onClick={() => this.downloadVideo(event)}><Icon
                                type="download"/>
                            </Button>
                        )}
                />

            </Table>
        )
    }

    downloadVideo(e) {
        AtccService.downloadVideo(e.id)
            .then((response) => {
                saveAs(response.data, e.timeStamp + ".mp4");
            }).catch(error => {
            alert("Something went wrong!");
        })
    }

    downloadImage(e) {
        AtccService.downloadScreenshot(e.id)
            .then((response) => {
                saveAs(response.data, e.timeStamp + ".jpg");
            }).catch(error => {
            alert("Something went wrong!");
        })
    }


}
