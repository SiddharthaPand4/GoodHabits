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
    Button, Menu, Dropdown, Typography, Input,
    notification
} from 'antd';
import GenericFilter from "../../components/GenericFilter";
import Moment from "react-moment";
import VidsService from "../../services/VidsService";
import feedService from "../../services/FeedService";
import {saveAs} from 'file-saver';
import {Player} from 'video-react';
import "video-react/dist/video-react.css";
import TrafficIncidentService from "../../services/TrafficIncidentService";


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
            incidentType: "",
            location: "All",
            feedID: 0,
            feedOptions: [],
            incidentOptions: [],
            incidents: {},
            playVideo: false,
            filter: {
                page: 1,
                pageSize: 12,
                incidentType: "",
                feedID: 0,

            },
        };

        this.refresh = this.refresh.bind(this);
        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.handleIncidentMenuClick = this.handleIncidentMenuClick.bind(this);

        this.handleLocationMenuClick = this.handleLocationMenuClick.bind(this);
        this.getFeeds = this.getFeeds.bind(this);
    }

    componentDidMount() {
        this.myInstant=setInterval(()=>{this.refresh()},30000);
        this.refresh();
        this.getFeeds();
        this.getIncidentTypes();
 }
    componentWillUnmount() {
        clearInterval(this.myInstant);
    }

    refresh() {
        this.UpdateIncidentFilter();
        this.UpdateLocationFilter();
        VidsService.getIncidents(this.state.filter).then(request => {
            this.setState({"incidents": request.data, loading: false})

        })
    }

    //cant use refresh to read from state as state may not have been set
    refreshNow() {

        this.UpdateIncidentFilter();
        this.UpdateLocationFilter();
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

    handleIncidentMenuClick(choice) {
        if (choice.item.props.children == "All") {
            this.setState({incidentType: ""})
        } else
            this.setState({incidentType: choice.item.props.children});
    }

    handleLocationMenuClick(choice) {
        let feedID;
        if (choice.item.props.children == "All") {
            feedID = 0;
            this.setState({feedID: feedID})
            this.setState({location: "All"})
        } else {
            feedID = choice.item.props.id;
            this.setState({feedID: feedID});
            this.setState({location: choice.item.props.children});


        }
    }

    downloadVideo(e) {
        VidsService.downloadVideo(e.id)
            .then((response) => {
                saveAs(response.data, e.timeStamp + ".mp4");
            }).catch(error => {
            alert("Something went wrong!");
        })
    }

    downloadImage(e) {
        VidsService.downloadScreenshot(e.id)
            .then((response) => {
                saveAs(response.data, e.timeStamp + ".jpg");
            }).catch(error => {
            alert("Something went wrong!");
        })
    }


    render() {

        let layout = this.state.layout;


        const incidentMenu = (
            <Menu onClick={this.handleIncidentMenuClick}>
                <Menu.Item key="1">
                    All
                </Menu.Item>
                {(this.state.incidentOptions || []).map((type) =>
                    <Menu.Item key={type}>
                        {type}
                    </Menu.Item>
                )}
            </Menu>
        );


        const locationMenu = (

            <Menu onClick={this.handleLocationMenuClick}>
                <Menu.Item key={1}>All</Menu.Item>
                {(this.state.feedOptions || []).map((feed) =>
                    <Menu.Item key={feed} id={feed.id}>
                        {feed.site + ">" + feed.location}
                    </Menu.Item>
                )}
            </Menu>

        );


        return (
            <div>
                <h3>Highway Incidents</h3>
                <Collapse bordered={false} defaultActiveKey={['1']}>
                    <span>&nbsp;&nbsp;</span>
                    <Panel header="Filter" key="1">
                        Location <Dropdown overlay={locationMenu}>
                        <Button color="#f50">
                            {this.state.location}<Icon type="down"/>
                        </Button>
                    </Dropdown>
                        <span>&nbsp;&nbsp;</span>
                        Incident Type <Dropdown overlay={incidentMenu}>
                        <Button color="#f50">
                            {this.state.incidentType}<Icon type="down"/>
                        </Button>
                    </Dropdown>

                        <br/>
                        <br/>
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

    getFeedString(event) {
        let result = "NA";
        if (event && event.feed) {
            result = event.feed.site + " > " + event.feed.location + " > " + event.feed.name;
        }
        return result;
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
                                                format="ll">{event.incidentDate}</Moment>{' '}|{' '}<Moment
                                                format="LTS">{event.incidentDate}</Moment></Text>
                                        </div>
                                        <div style={{marginTop: "5px", textAlign: "left"}}>
                                            <div>
                                                <Text code>
                                                    <Icon type="environment"/>
                                                    {this.getFeedString(event)}
                                                </Text>
                                            </div>
                                        </div>

                                    </div>

                                }

                                extra={<Dropdown overlay={<Menu>
                                    {
                                        (event.incidentImage!="")?
                                        <Menu.Item key="1">
                                        <a
                                            title={"click here to download"}
                                            onClick={() => this.downloadImage(event)}
                                        ><Icon type="download"/>{' '} Image</a>
                                    </Menu.Item>
                                            : null
                                    }
                                    {(event.incidentVideo!="")?
                                        <Menu.Item key="2">
                                            <a
                                                title={"click here to download"}
                                                onClick={() => this.downloadVideo(event)}
                                            ><Icon type="download"/>{' '} Video</a>
                                        </Menu.Item>
                                        :null
                                    }
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

                            >


                                <Player
                                    playsInline
                                    poster={"/public/vids/image/" + event.id + "/image.jpg"}
                                    src={"/public/vids/video/" + event.id + "/video.mp4"}
                                />
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
                                     src={"/public/anpr/lpr/" + id + "/image.jpg"}/></a>)}/>
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

    UpdateIncidentFilter() {
        let filter = this.state.filter;
        filter.incidentType = this.state.incidentType;
        this.setState({filter: filter})
    }

    UpdateLocationFilter() {
        let filter = this.state.filter;
        filter.feedID = this.state.feedID;
        this.setState({filter: filter});

    }

    getFeeds() {
        feedService.getFeeds().then(response => {
            this.setState({feedOptions: response.data});
        }).catch(error => {
            notification.open({
                message: 'Something went wrong ',
                discription: error
            });
        })
    }


    getIncidentTypes() {

        TrafficIncidentService.getIncidentTypes().then(response => {
            this.setState({incidentOptions: response.data});

        }).catch(error => {
            notification.open({
                message: 'Something went wrong ',
                discription: error
            });
        })

    }

}
