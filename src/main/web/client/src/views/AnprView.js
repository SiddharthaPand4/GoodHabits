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
    message, Input
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
            filter: {
                page: 1,
                pageSize: 10
            }
        };

        this.refresh = this.refresh.bind(this);
        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.onLprInputChange = this.onLprInputChange.bind(this);
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

    onLprInputChange(e) {

        let filter = this.state.filter;
        filter.lpr = e.target.value;
        console.log(filter);
        this.setState({filter:filter})
    }

    handleFilterChange(data){
        this.setState({filter:data})
    }

     handleLayoutChange(data){
         this.setState({layout:data})
     }

     handleRefresh(){
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
        let lpr = this.state.filter.lpr;

        return (<div>ANPR
            <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                <Panel header="Filter" key="1">
                    LPR: <Input value={lpr} style={{ "width": "200px"}} onChange={this.onLprInputChange}/> <br/><br/>
                    <GenericFilter handleRefresh={this.refresh} filter={this.state.filter} layout={layout} handleFilterChange={this.handleFilterChange} handleLayoutChange={this.handleLayoutChange}/>
                </Panel>
                <Panel header="Events" key="2">
                    {layout === "table" ? (this.renderTable()) :(this.renderGrid()) }
                </Panel>
            </Collapse>
        </div>)
    }

    renderGrid() {

        if (this.state.loading || !this.state.anprresponse || this.state.anprresponse.totalPage === 0) {
            return <Empty description={false}/>
        }

        let events = this.state.anprresponse.events;
        let count =  this.state.anprresponse.totalPages *  this.state.anprresponse.pageSize;

        return <div style={{background: '#ECECEC', padding: '30px'}}>
            <Row>
                <Col>
                    <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange} showSizeChanger showQuickJumper
                              defaultCurrent={1}  total={count} current={this.state.filter.page} pageSize ={this.state.filter.pageSize}/>
                </Col>
            </Row>

            <Row gutter={16}>
                {
                    events.map((event, index) =>
                        <Col span={8} key={index}>
                            <Card
                                title={
                                    <div>
                                        <Tag color="#2db7f5"><Moment format="L">{event.eventDate}</Moment></Tag>
                                        <Tag color="#87d068"><span><Moment format="LTS">{event.eventDate}</Moment></span><Icon
                                            type="right" hidden/><span hidden><Moment
                                            format="LTS">{event.eventDate}</Moment></span></Tag>
                                        <Tag>{event.anprText}</Tag>
                                    </div>
                                }
                                bordered={true}
                                cover={<img alt="event"
                                            src={"/public/anpr/vehicle/" + event.id + "/image.jpg"}/>}
                            >
                                <img alt="event"
                                     src={"/public/anpr/lpr/" + event.id + "/image.jpg"}/>
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
        let count = this.state.anprresponse.totalPages *  this.state.anprresponse.pageSize;

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
                <Column title="Date" dataIndex="eventDate" key="eventDate"
                        render={eventDate => (<Moment format="L">{eventDate}</Moment>)}/>
                <Column title="Time" dataIndex="eventDate" key="eventTime"
                        render={eventDate => (<Moment format="LTS">{eventDate}</Moment>)}/>
                <Column title="LPR" dataIndex="anprText" key="anprText"
                        render={anprText => anprText}/>
                <Column title="image" dataIndex="id" key="anprimage"
                        render={id => (<img alt="event" src={"/public/anpr/lpr/" + id + "/image.jpg"}/>)}/>

            </Table>
        )
    }
}
