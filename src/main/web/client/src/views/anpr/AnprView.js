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
    message, Form, Spin
} from 'antd';
import GenericFilter from "../../components/GenericFilter";
import Moment from "react-moment";
import AnprService from "../../services/AnprService";
import Magnifier from "react-magnifier";
import moment from "moment";
import {saveAs} from "file-saver";
import AnprReportService from "../../services/AnprReportService";

const {Paragraph, Text} = Typography;

const {Column} = Table;
const {Panel} = Collapse;


export default class AnprView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activePanelKey: ["1"],
            visible: true,
            loading: true,
            layout: "list",
            events: {},
            filter: {
                page: 1,
                pageSize: 12,
                lpr: ""

            },
            workingEvent: {},
            workingEventLoading: false,
            magnifyEvent: {
                magnifyEventId: "",
                zoomFactor: 2,
                minZoomFactor: 1,
                maxZoomFactor: 5
            },
            downloading: false,
            pageSizeOptions: ['12', '24', '48', '100', "250", "500", "1000", "2500", "5000"]
        };

        this.refresh = this.refresh.bind(this);

        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.onLprInputChange = this.onLprInputChange.bind(this);
        this.editEvent = this.editEvent.bind(this);
        this.updateEvent = this.updateEvent.bind(this);
        this.magnifyEvent = this.magnifyEvent.bind(this);
        this.updateZoomFactor = this.updateZoomFactor.bind(this);
        this.downloadAnprReport = this.downloadAnprReport.bind(this);

        this.preparePrint = this.preparePrint.bind(this);
        this.onCollapse = this.onCollapse.bind(this);

    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        this.setState({loading: true});
        AnprService.getEvents(this.state.filter).then(response => {
            this.setState({"anprresponse": response.data, loading: false})
        }).catch(error => {
            alert("Something went wrong!");
            this.setState({loading: false});
        })
    }

    archiveEvent(event) {
        AnprService.archiveEvent(event).then(request => {
            this.refresh();
        })
    }

    onLprInputChange(e) {

        let filter = this.state.filter;
        filter.lpr = e.target.value;
        this.setState({filter: filter})
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

    updateEvent(anprText) {

        let {workingEvent, workingEventLoading} = this.state;
        workingEvent.anprText = anprText;
        workingEventLoading = true;
        this.setState({workingEvent, workingEventLoading});
        AnprService.updateEvent(workingEvent).then(request => {
            let {workingEvent, workingEventLoading} = this.state;
            workingEvent.anprText = anprText;
            workingEventLoading = false;
            this.setState({workingEventLoading});
        }).catch(error => {
            alert("error in saving");
            let {workingEventLoading} = this.state;
            workingEventLoading = false;
            this.setState({workingEventLoading});
        })
    }

    downloadAnprReport() {
        this.setState({downloading: true})
        let filter = this.state.filter;
        let req = {
            fromDateString: filter.from_date != null ? moment(filter.fromDate).format('YYYY-MM-DD HH:mm:ss') : "",
            toDateString: filter.to_date != null ? moment(filter.toDate).format('YYYY-MM-DD HH:mm:ss"') : "",
            lpr: filter.lpr,
        };

        AnprReportService.getAnprEventsReport(req).then(response => {
            this.setState({downloading: false});
            saveAs(response.data, "anpr-events.csv");
        }).catch(error => {
            this.setState({downloading: false});
        });
    }

    preparePrint() {
        window.print();
    }


    onCollapse(change) {
        this.setState({activePanelKey: change})
    }

    render() {

        let layout = this.state.layout;
        let lpr = this.state.filter.lpr;
        let activePanelKey = this.state.activePanelKey;

        return (
            <div>
                <h3>ANPR</h3>


                <div>
                    <Collapse className={"no-print"} bordered={false} defaultActiveKey={['1']}
                              activeKey={activePanelKey}
                              onChange={(e) => this.onCollapse(e)}>
                        <Panel header="Filter" key="1">

                            LPR: <Input value={lpr} style={{"width": "200px"}}
                                        onChange={this.onLprInputChange}/>&nbsp;&nbsp;
                            <Button type="dashed" style={{float: "right"}} onClick={() => {
                                this.downloadAnprReport()
                            }}><Icon type="file-excel"/></Button>
                            <Button type="dashed" style={{float: "right"}} onClick={this.preparePrint}>Print <Icon
                                type="printer"/></Button>

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


        if (this.state.loading || !this.state.anprresponse || this.state.anprresponse.totalPage === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.anprresponse.events;
        let workingEventLoading = this.state.workingEventLoading;
        let workingEvent = this.state.workingEvent;
        let count = this.state.anprresponse.totalPages * this.state.anprresponse.pageSize;

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
                                        {(event.vehicleClass) ? <Tag color="#f50">{event.vehicleClass}</Tag> : null}
                                        {(event.direction && event.direction === "rev") ?
                                            <Tag color="#f50">Reverse</Tag> : null}
                                        {(event.helmet) ? <Tag color="#f50">Without helmet</Tag> : null}

                                        {(event.sectionSpeed) ? <Tag color="#f50">Overspeeding</Tag> : null}
                                    </div>
                                }
                                extra={<Dropdown overlay={<Menu>
                                    <Menu.Item key="0" onClick={() => this.magnifyEvent(event)}><Icon type="zoom-in"/>Zoom
                                        image
                                    </Menu.Item>
                                    <Menu.Item key="1">
                                        <a
                                            title={"click here to download"}
                                            href={"/public/anpr/vehicle/" + event.id + "/image.jpg"}
                                            download={true}><Icon type="download"/>{' '} Full
                                            image</a>
                                    </Menu.Item>
                                    <Menu.Item key="2">
                                        <a
                                            title={"click here to download"}
                                            href={"/public/anpr/lpr/" + event.id + "/image.jpg"}
                                            download={true}><Icon type="download"/>{' '} Cropped image</a>
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
                                cover={(magnifyEventId === event.id) ?
                                    <Magnifier src={"/public/anpr/vehicle/" + event.id + "/image.jpg"}
                                               zoomFactor={zoomFactor}/> : <img alt="event"
                                                                                src={"/public/anpr/vehicle/" + event.id + "/image.jpg"}/>

                                }
                            >
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
                                            <Button size="small" type="dashed" onClick={() => this.magnifyEvent(event)}>
                                                <Icon type="zoom-in"/>Zoom Image
                                            </Button>
                                        </div>

                                    }
                                </div>
                                <div style={{textAlign: "center"}}>
                                    <img alt="event"
                                         src={"/public/anpr/lpr/" + event.id + "/image.jpg"}/>
                                </div>
                                <div style={{marginTop: "5px", textAlign: "center"}}
                                     onClick={() => this.editEvent(event)}>
                                    <Paragraph
                                        strong
                                        editable={{onChange: this.updateEvent}}
                                        copyable>{event.anprText}</Paragraph>
                                    <Text
                                        type="secondary">{(workingEventLoading && workingEvent.id === event.id) ? "saving..." : ""}</Text>
                                    <Text
                                        type="secondary">{(event.speed) ? "Speed: " + event.speed : ""}</Text>
                                    <div>
                                        <Text code><Icon type="schedule"/> <Moment
                                            format="ll">{event.eventDate}</Moment>{' '}|{' '}<Moment
                                            format="LTS">{event.eventDate}</Moment></Text>
                                    </div>
                                    <div>
                                        <Text code><Icon type="environment"/> {event.location}</Text>
                                    </div>


                                </div>

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

        let events = this.state.anprresponse.events;
        let count = this.state.anprresponse.totalPages * this.state.anprresponse.pageSize;

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
                <Column title="Location" dataIndex="location" key="location"
                        render={location => location}/>
                <Column title="Date" dataIndex="eventDate" key="eventDate"
                        render={eventDate => (<Moment format="L">{eventDate}</Moment>)}/>
                <Column title="Time" dataIndex="eventDate" key="eventTime"
                        render={eventDate => (<Moment format="LT">{eventDate}</Moment>)}/>
                <Column title="LPR" dataIndex="anprText" key="anprText"
                        render={anprText => anprText}/>
                <Column title="LP Image" dataIndex="id" key="anprimage"
                        render={id => (
                            <a title={"click here to download"} href={"/public/anpr/lpr/" + id + "/image.jpg"}
                               download={true}>
                                <img alt="event"
                                     style={{maxWidth: 120}}
                                     src={"/public/anpr/lpr/" + id + "/image.jpg"}/></a> )}/>
                <Column title="Direction" dataIndex="direction" key="direction"
                        render={direction => direction}/>
                <Column title="Helmet?" dataIndex="helmet" key="helmet"
                        render={helmet => helmet ? <span>No</span> : <span>N/A</span>}/>
                <Column title="Speed" dataIndex="speed" key="speed"
                        render={speed => <span>{speed}</span>}/>
                <Column title="Action"
                        key="action"
                        render={(text, event) => (
                            <Button type="danger" title={"Archive"} onClick={() => this.archiveEvent(event)}><Icon
                                type="warning"/>{' '}
                            </Button>
                        )}
                />

            </Table>
        )
    }
}
