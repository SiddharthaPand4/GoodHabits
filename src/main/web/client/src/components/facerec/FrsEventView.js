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
    Input, Button, Menu, Dropdown, Typography, Spin, Select
} from 'antd';
import GenericFilter from "../../components/GenericFilter";
import Moment from "react-moment";
import moment from "moment";
import {saveAs} from "file-saver";
import FrsService from "../../services/facerec/FrsService";

const {Text} = Typography;

const {Column} = Table;
const {Panel} = Collapse;


export default class FrsEventView extends Component {

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
                name: "",
                match: "",
                type: "",
                accessType: ""
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
            pageSizeOptions: ['12', '24', '48', '100', "250", "500"]
        };

        this.refresh = this.refresh.bind(this);

        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.onNameInputChange = this.onNameInputChange.bind(this);
        this.editEvent = this.editEvent.bind(this);
        this.downloadEventReport = this.downloadEventReport.bind(this);

        this.preparePrint = this.preparePrint.bind(this);
        this.onCollapse = this.onCollapse.bind(this);

        this.handleTypeChange = this.handleTypeChange.bind(this);
        this.handleAccessTypeChange = this.handleAccessTypeChange.bind(this);
        this.handleMatchChange = this.handleMatchChange.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        this.setState({loading: true});
        FrsService.getEvents(this.state.filter).then(response => {
            this.setState({"frsresponse": response.data, loading: false})
        }).catch(error => {
            alert("Something went wrong!");
            this.setState({loading: false});
        })
    }

    archiveEvent(event) {
        FrsService.archiveEvent(event).then(request => {
            this.refresh();
        })
    }

    onNameInputChange(e) {

        let filter = this.state.filter;
        filter.name = e.target.value;
        this.setState({filter: filter})
    }

    handleTypeChange(value) {
        let filter = this.state.filter;
        filter.type = value;
        this.setState({filter: filter})
    }

    handleMatchChange(value) {
        let filter = this.state.filter;
        filter.match = value;
        this.setState({filter: filter})
    }

    handleAccessTypeChange(value) {
        let filter = this.state.filter;
        filter.accessType = value;
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

    downloadEventReport() {
        this.setState({downloading: true})
        let filter = this.state.filter;
        let req = {
            fromDateString: filter.from_date != null ? moment(filter.fromDate).format('YYYY-MM-DD HH:mm:ss') : "",
            toDateString: filter.to_date != null ? moment(filter.toDate).format('YYYY-MM-DD HH:mm:ss"') : "",
            name: filter.name,
        };

        FrsService.getEventsReport(req).then(response => {
            this.setState({downloading: false});
            saveAs(response.data, "frs-events.csv");
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
        let layout = "yahoo!";
        let name = this.state.filter.name;
        let activePanelKey = this.state.activePanelKey;

        return (
            <div>
                <h3>Events</h3>
                <div>
                    <Collapse className={"no-print"} bordered={false} defaultActiveKey={['1']}
                              activeKey={activePanelKey}
                              onChange={(e) => this.onCollapse(e)}>
                        <Panel header="Filter" key="1">

                            Name: <Input value={name} style={{"width": "200px"}}
                                         onChange={this.onNameInputChange}/>&nbsp;&nbsp;

                            Match: <Select defaultValue="" onChange={this.handleMatchChange}>
                            <Select.Option value="">--Select--</Select.Option>
                            <Select.Option value="Matched">Matched</Select.Option>
                            <Select.Option value="Unmatched">Unmatched</Select.Option>
                        </Select>

                            Type: <Select defaultValue="" onChange={this.handleTypeChange}>
                            <Select.Option value="">--Select--</Select.Option>
                            <Select.Option value="Employee">Employee</Select.Option>
                            <Select.Option value="Vendor">Vendor</Select.Option>
                            <Select.Option value="Visitor">Visitor</Select.Option>
                            <Select.Option value="Others">Others</Select.Option>
                        </Select>

                            Access Type: <Select defaultValue="" onChange={this.handleAccessTypeChange}>
                            <Select.Option value="">--Select--</Select.Option>
                            <Select.Option value="BlackList">BlackList</Select.Option>
                            <Select.Option value="WhiteList">WhiteList</Select.Option>
                        </Select>

                            <Button type="dashed" style={{float: "right"}} onClick={() => {
                                this.downloadEventReport()
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

        if (this.state.loading || !this.state.frsresponse || this.state.frsresponse.totalPage === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.frsresponse.events;
        let workingEventLoading = this.state.workingEventLoading;
        let workingEvent = this.state.workingEvent;
        let count = this.state.frsresponse.totalPages * this.state.frsresponse.pageSize;


        return <div style={{background: '#ECECEC', padding: '5px'}}>
            <Row>
                {
                    events.map((event, index) =>
                        <Col xl={{span: 8}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}} key={index}>
                            <Card
                                style={{margin: "5px"}}
                                title={
                                    <div>
                                        <Tag color="#f50">{event.type}</Tag>
                                        {(event.person) ? <Tag color="#f50">{event.person.type}</Tag> : ""}
                                        {(event.person) ? <Tag color="#f50">{event.person.accessType}</Tag> : ""}
                                        <br/>
                                        <Text code><Icon type="schedule"/> <Moment
                                            format="ll">{event.eventDate}</Moment>{' '}|{' '}<Moment
                                            format="LTS">{event.eventDate}</Moment></Text><br/>
                                        <Text
                                            type="secondary">{(event.person) ? "ID: " + event.person.pid : ""}</Text>
                                        <Text
                                            type="secondary">{(event.person) ? "  Name: " + event.person.name : ""}</Text>

                                    </div>
                                }
                                bordered={true}
                            >
                                <div style={{textAlign: "center"}}>
                                    <img alt="face" style={{width: 100, height: 100, borderRadius: "50%"}}
                                         src={"/public/frs/event/face/" + event.eventId + "/image.jpg"}/>
                                </div>
                                <div style={{marginTop: "5px", textAlign: "center"}}>
                                    <div style={{textAlign: "center"}}>
                                        <img alt="person" style={{width: 200, height: 200}}
                                             src={"/public/frs/event/full/" + event.eventId + "/image.jpg"}/>
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

        let events = this.state.frsresponse.events;
        let count = this.state.frsresponse.totalPages * this.state.frsresponse.pageSize;

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
                        render={name => name}/>
                <Column title="LP Image" dataIndex="id" key="anprimage"
                        render={id => (
                            <a title={"click here to download"} href={"/public/anpr/lpr/" + id + "/image.jpg"}
                               download={true}>
                                <img alt="event"
                                     style={{maxWidth: 120}}
                                     src={"/public/frs/face/" + id + "/image.jpg"}/></a>)}/>

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
