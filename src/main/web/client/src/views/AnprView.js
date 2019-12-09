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
    Modal,
    message, Input, Button, Menu, Dropdown, Typography
} from 'antd';
import GenericFilter from "../components/GenericFilter";
import Moment from "react-moment";
import AnprService from "../services/AnprService";
import {Link} from "react-router-dom";
import FileSaver from 'file-saver';

const {Paragraph, Text} = Typography;

const {Column} = Table;
const {Panel} = Collapse;


export default class AnprView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            layout: "list",
            events: {},
            filter: {
                page: 1,
                pageSize: 24
            }
        };

        this.refresh = this.refresh.bind(this);
        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.onLprInputChange = this.onLprInputChange.bind(this);
        this.updateEvent = this.updateEvent.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        AnprService.getEvents(this.state.filter).then(request => {
            this.setState({"anprresponse": request.data, loading: false})
        })
    }

    //cant use refresh to read from state as state may not have been set
    refreshNow(filter) {
        AnprService.getEvents(this.state.filter).then(request => {
            this.setState({"anprresponse": request.data, loading: false})
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
        console.log(filter);
        this.setState({filter: filter})
    }

    handleFilterChange(data) {
        this.setState({filter: data})
    }

    handleLayoutChange(data) {
        this.setState({layout: data})
    }

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

    updateEvent(event){
        AnprService.updateEvent(event).then(request => {

        }).catch(error=>{
            alert("error in saving");
        })
    }


    render() {

        let layout = this.state.layout;
        let lpr = this.state.filter.lpr;

        return (<div>ANPR
            <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                <Panel header="Filter" key="1">
                    LPR: <Input value={lpr} style={{"width": "200px"}} onChange={this.onLprInputChange}/> <br/><br/>
                    <GenericFilter handleRefresh={this.refresh} filter={this.state.filter} layout={layout}
                                   handleFilterChange={this.handleFilterChange}
                                   handleLayoutChange={this.handleLayoutChange}/>
                </Panel>
                <Panel header="Events" key="2">
                    {layout === "table" ? (this.renderTable()) : (this.renderGrid())}
                </Panel>
            </Collapse>
        </div>)
    }

    renderGrid() {


        if (this.state.loading || !this.state.anprresponse || this.state.anprresponse.totalPage === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.anprresponse.events;
        let count = this.state.anprresponse.totalPages * this.state.anprresponse.pageSize;

        return <div style={{background: '#ECECEC', padding: '30px'}}>
            <Row>
                <Col>
                    <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange} showSizeChanger
                                showQuickJumper
                                defaultCurrent={1} total={count} current={this.state.filter.page}
                                pageSize={this.state.filter.pageSize}/>
                </Col>
            </Row>

            <Row>
                {
                    events.map((event, index) =>
                        <Col xl={{span: 8}} lg={{span: 12}} md={{span: 16}} sm={{span: 20}} xs={{span: 20}} key={index}>
                            <Card
                                style={{margin: "5px"}}
                                title={
                                    <div>
                                        {(event.direction && event.direction === "rev") ?
                                            <Tag color="#f50">Reverse</Tag> : null}
                                        {event.helmet ? <Tag><span>Helmet:No</span></Tag> : null}


                                    </div>
                                }
                                extra={<Dropdown overlay={<Menu>
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
                                            Delete
                                        </Button>
                                    </Menu.Item>

                                </Menu>}>
                                    <Button>
                                        More <Icon type="down"/>
                                    </Button>
                                </Dropdown>}
                                bordered={true}
                                cover={
                                    <img alt="event"
                                         src={"/public/anpr/vehicle/" + event.id + "/image.jpg"}/>}
                            >
                                <div style={{textAlign: "center"}}>
                                    <img alt="event"
                                         src={"/public/anpr/lpr/" + event.id + "/image.jpg"}/>
                                </div>
                                <div style={{marginTop: "5px", textAlign: "center"}} onClick={}>
                                    <Paragraph
                                        strong
                                        editable={{onChange: ()=> this.updateEvent()}} copyable>{event.anprText}</Paragraph>
                                    <div>
                                        <Text code> <Moment format="L">{event.eventDate}</Moment>{' '}|{' '}<Moment
                                            format="LTS">{event.eventDate}</Moment></Text>
                                    </div>
                                </div>

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
