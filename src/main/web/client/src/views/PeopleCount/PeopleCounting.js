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
    message, Input, Button, Menu, Dropdown, Typography, Slider
} from 'antd';

import GenericFilter from "../../components/GenericFilter";
import Moment from "react-moment";
import ApcFileService from "../../services/ApcFileService";
import Magnifier from "react-magnifier";
const {Paragraph, Text} = Typography;
const {Column} = Table;
const {Panel} = Collapse;

export default class PeopleCounting extends Component{

    constructor(props) {
        super(props);
        this.state = {

                    loading: true,
                    layout: "list",
                    events: {},
                    filter: {
                        page: 1,
                        pageSize: 24

                    },

                      workingEvent: {},
                                workingEventLoading: false,
                                magnifyEvent: {
                                    magnifyEventId: "",
                                    zoomFactor: 2,
                                    minZoomFactor: 1,
                                    maxZoomFactor: 5
                                },

                };

        this.refresh = this.refresh.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.handleFilterChange=this.handleFilterChange.bind(this);
        this.handleLayoutChange=this.handleLayoutChange.bind(this);
        this.editEvent = this.editEvent.bind(this);
        this.magnifyEvent = this.magnifyEvent.bind(this);
        this.updateZoomFactor = this.updateZoomFactor.bind(this);
        this.onPcIdInputChange=this.onPcIdInputChange.bind(this);
    }

    componentDidMount(){
        this.refresh();
    }


    onPageSizeChange(current, pageSize) {
        let filter = this.state.filter;
        filter.page=current;// Here i made change
        filter.pageSize = pageSize;
        this.refresh(filter);
    }
    handleLayoutChange(data) {
        this.setState({layout: data})
    }

    refresh(filter) {
        if(!filter){
                filter = this.state.filter;
        }
         this.setState({loading: true});
         ApcFileService.getPeopleData(filter).then(response => {
             let data = response.data ;
             filter.pageSize   =   data.pageSize;
             filter.currentPage =   data.pageNumber;
             filter.totalPages =   data.totalPages;

            this.setState({filter, loading: false, events: data.list});

        }).catch(error => {
            alert("Error, something went wrong!!");

        })
    }

    handleFilterChange(data) {
        this.setState({filter: data})
    }

    handleRefresh() {
        this.refresh(this.state.refresh);
    }

    onPageChange(page, pageSize) {
        let filter = this.state.filter;
        filter.page = page;
        filter.pageSize = pageSize;
        this.refresh(filter)
    }

    editEvent(event) {
            this.setState({workingEvent: event});
        }

     archiveEvent(event){
        ApcFileService.archiveEvent(event).then(request => {
        this.refresh();
    })

     }
      onPcIdInputChange(e) {

             let filter = this.state.filter;
             filter.pcId = e.target.value;
             console.log(filter);
             this.setState({filter: filter})
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
    render() {

             let layout = this.state.layout;
             let pcId=this.state.filter.pcId;
            return (

            <div>

                 <Collapse bordered={true} defaultActiveKey={['1']}>
                                <Panel header="Filter" key="1">
                                Event Id: <Input value={pcId} style={{"width": "200px"}} onChange={this.onPcIdInputChange}/> <br/><br/>
                                    <GenericFilter handleRefresh={this.refresh} filter={this.state.filter} layout={layout}
                                                   handleFilterChange={this.handleFilterChange}
                                                   handleLayoutChange={this.handleLayoutChange}
                                                   />
                                </Panel>
                  </Collapse>

                  <div>
                    {layout === "table" ? (this.renderTable()) : (this.renderGrid())}
                    </div>


         </div>
            );
        }


        renderTable(){

        if (this.state.loading || !this.state.events || this.state.events.length === 0) {
                        return <Empty description={false}/>
                    }
                    let events = this.state.events;
                    let count = this.state.filter.totalPages * this.state.filter.pageSize;

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
                        current: this.state.filter.currentPage,
                        pageSize: this.state.filter.pageSize
                    };
           return (    <div>
                        <Table dataSource={events} pagination={pagination}>
                             <Column title="ID" dataIndex="eventId" key="eventId"
                                     render={eventId =><Paragraph strong copyable>{eventId}</Paragraph>}/>
                             <Column title="Date" dataIndex="eventDate" key="eventDate"
                                     render={eventDate => (<Moment format="L">{eventDate}</Moment>)}/>
                             <Column title="Archived" dataIndex="archived" key="archived"
                                     render={ archived=>(<p>{archived ? "Archived": "Active"}</p>)}/>
                               <Column title="Direction" dataIndex="direction" key="direction"
                                       render={direction => direction }/>
                      </Table>
                    </div>
                       );



        }

   renderGrid() {


              if (this.state.loading || !this.state.events || this.state.events.length === 0) {
                    return <Empty description={false}/>
                }

                let events = this.state.events;
                let workingEventLoading = this.state.workingEventLoading;
                let workingEvent = this.state.workingEvent;
                let count = this.state.filter.totalPages * this.state.filter.pageSize;

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

                                        extra={<Dropdown overlay={<Menu>
                                            <Menu.Item key="0" onClick={() => this.magnifyEvent(event)}><Icon type="zoom-in"/>Zoom
                                                image
                                            </Menu.Item>
                                            <Menu.Item key="1">
                                                <a
                                                    title={"click here to download"}
                                                    href={"/public/apc/people/" + event.id + "/image.jpg"}
                                                    download={true}><Icon type="download"/>{' '} Full
                                                    image</a>
                                            </Menu.Item>
                                            <Menu.Item key="2">
                                                <a
                                                    title={"click here to download"}
                                                    href={"/public/apc/people/" + event.id + "/image.jpg"}
                                                    download={true}><Icon type="download"/>{' '} Cropped image</a>
                                            </Menu.Item>
                                            <Menu.Item key="3">
                                                <Button type="danger" onClick={() => this.archiveEvent(event)}><Icon
                                                    type="delete"/>{' '}
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
                                            <Magnifier src={"/public/apc/people/" + event.eventId + "/image.jpg"}
                                                       zoomFactor={zoomFactor}/> : <img alt="event"
                                                                            src={"/public/apc/people/" + event.eventId + "/image.jpg"}/>

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
                                                : <div style={{height:"54px",textAlign: "center"}}>
                                                       <Button size="small" type="primary" onClick={() => this.magnifyEvent(event)} >
                                                           <Icon type="zoom-in"/>Zoom Image
                                                       </Button>
                                                   </div>
                                            }
                                        </div>

                                        <div style={{marginTop: "5px", textAlign: "center"}}
                                             onClick={() => this.editEvent(event)}>

                                            <Text
                                                type="secondary">{(workingEventLoading && workingEvent.id === event.eventId) ? "saving..." : ""}</Text>
                                            <div>
                                            <Paragraph
                                              strong
                                              copyable>{event.eventId}</Paragraph>
                                            </div>
                                            <div>
                                                <Text code> <Moment format="ll">{event.eventDate}</Moment>{' '}|{' '}<Moment
                                                    format="LTS">{event.eventDate}</Moment></Text>
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
                                    pageSize={this.state.filter.pageSize}/>
                    </div>

                </div>
            }


}