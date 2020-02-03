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
        this.onEventIdInputChange=this.onEventIdInputChange.bind(this);

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
         message.error("Error, something went wrong!!")
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
      onEventIdInputChange(e) {

             let filter = this.state.filter;
             filter.eventId = e.target.value;
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
             let eventId=this.state.filter.eventId;
            return (

            <div>

                 <Collapse bordered={true} defaultActiveKey={['1']}>
                                <Panel header="Filter" key="1">
                                Event Id: <Input value={eventId} style={{"width": "200px"}} onChange={this.onEventIdInputChange}/> <br/><br/>
                                    <GenericFilter handleRefresh={this.refresh} filter={this.state.filter} layout={layout}
                                                   handleFilterChange={this.handleFilterChange}
                                                   handleLayoutChange={this.handleLayoutChange}
                                                   />
                                </Panel>
                  </Collapse>

                  <div>
                    {this.renderTable()}
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



}