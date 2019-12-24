import React, { Component } from "react";
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
  Tabs,
  message,
  Input,
  Button,
  Menu,
  Dropdown,
  Typography,
  Slider
} from "antd";
import GenericFilter from "../components/GenericFilter";
import Moment from "react-moment";
import AnprService from "../services/AnprService";
import Magnifier from "react-magnifier";

const { Panel } = Collapse;
const { Paragraph, Text } = Typography;
const {Column} = Table;
const { TabPane } = Tabs;

export default class IncidentRepeatedView extends Component {
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

    this.handleRefresh = this.handleRefresh.bind(this);
    this.onPageChange = this.onPageChange.bind(this);
    this.onPageSizeChange = this.onPageSizeChange.bind(this);
    this.handleTabClick = this.handleTabClick.bind(this);
  }
  handleTabClick(tabIndex) {
    this.setState({
      activeTabIndex:
        tabIndex === this.state.activeTabIndex
          ? this.props.defaultActiveTabIndex
          : tabIndex
    });
  }
  componentDidMount() {
    this.refresh();
  }

  refresh() {
    AnprService.getIncidentsRepeated(this.state.filter).then(request => {
      this.setState({
        anprresponse: request.data,
        loading: false
      });
    });
  }

  //cant use refresh to read from state as state may not have been set
  refreshNow(filter) {
    AnprService.getIncidentsRepeated(this.state.filter).then(request => {
      this.setState({
        anprresponse: request.data,
        loading: false
      });
    });
  }

  handleRefresh() {
    this.refresh();
  }

  onPageChange(page, pageSize) {
    let filter = this.state.filter;
    filter.page = page;
    filter.pageSize = pageSize;
    this.refreshNow(filter);
  }

  onPageSizeChange(current, pageSize) {
    let filter = this.state.filter;
    filter.pageSize = pageSize;
    this.refreshNow(filter);
  }

  updateEvent(anprText) {
    let { workingEvent, workingEventLoading } = this.state;
    workingEvent.anprText = anprText;
    workingEventLoading = true;
    this.setState({
      workingEvent,
      workingEventLoading
    });
    AnprService.updateEvent(workingEvent)
      .then(request => {
        let { workingEvent, workingEventLoading } = this.state;
        workingEvent.anprText = anprText;
        workingEventLoading = false;
        this.setState({
          workingEventLoading
        });
      })
      .catch(error => {
        alert("error in saving");
        let { workingEventLoading } = this.state;
        workingEventLoading = false;
        this.setState({
          workingEventLoading
        });
      });
  }

  render() {
    let layout = this.state.layout;
    let lpr = this.state.filter.lpr;

    return (
      <Tabs defaultActiveKey="1">
        <TabPane tab="Reverse" key="1">
          <div>
            {this.renderReverseData()}
          </div>
        </TabPane>
        <TabPane tab="Helmet-Missing" key="2">
          <div>
            helmet missing div
              {this.renderHelmetMissingData()}
          </div>
        </TabPane>
      </Tabs>
    );
  }

  renderReverseData(){
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
   <div> hello this is reverse table
   <Table dataSource={events} pagination={pagination}>

               if(key=reverse)

              <Column title="LPR" dataIndex="anprText" key="anprText"
                      render={anprText => anprText}/>
               <Column title="count" dataIndex="repeatedTimes" key="repeatedTimes"
                      render={repeatedTimes => repeatedTimes}/>
          </Table>
    </div>

   )
  }

  renderHelmetMissingData(){
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
   <div> hello this is helmet missing table
   <Table dataSource={events} pagination={pagination}>

               if(key=reverse)

              <Column title="LPR" dataIndex="anprText" key="anprText"
                      render={anprText => anprText}/>
               <Column title="count" dataIndex="repeatedTimes" key="repeatedTimes"
                      render={repeatedTimes => repeatedTimes}/>
          </Table>
    </div>

   )
  }





}
