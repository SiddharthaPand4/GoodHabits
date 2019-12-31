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
      helmetMissing:{
        loading:false,
        anprresponse: {},
      filter: {
        page: 1,
        pageSize: 24
      }
     },
      reverseDirection:{
        loading:false,
        anprresponse: {},
        filter: {
          pages: 1,
          pageSizes: 24
        }
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
     let helmetMissing = this.state.helmetMissing;
     let reverseDirection = this.state.reverseDirection;
     helmetMissing.loading = true;
     reverseDirection.loading=true;
     this.setState({helmetMissing:helmetMissing});
     this.setState({reverseDirection:reverseDirection});
     AnprService.getHelmetMissingIncidentsRepeated(this.state.helmetMissing.filter).then(request => {

       helmetMissing.loading = false;
       helmetMissing.anprresponse= request.data;

       this.setState({helmetMissing:helmetMissing});
     }).catch(error=> {
                          helmetMissing.loading = false;
                          this.setState({helmetMissing:helmetMissing});
            });

      AnprService.getReverseDirectionIncidentsRepeated(this.state.reverseDirection.filter).then(request => {
             reverseDirection.loading = false;
             reverseDirection.anprresponse= request.data;

             this.setState({reverseDirection:reverseDirection});
           }).catch(error=> {
                                reverseDirection.loading = false;
     this.setState({reverseDirection:reverseDirection});
    });
  }

  //cant use refresh to read from state as state may not have been set
  refreshHelmetMissingIncidentsNow(filter) {
    AnprService.getHelmetMissingIncidentsRepeated(this.state.helmetMissing.filter).then(request => {
      this.setState({
        anprresponse: request.data,
        loading: false
      });
    });
  }
  refreshReverseDirectionIncidentsNow(filter) {
      AnprService.getReverseDirectionIncidentsRepeated(this.state.reverseDirection.filter).then(request => {
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
    let filter = this.state.helmetMissing.filter;
    filter.page = page;
    filter.pageSize = pageSize;
    this.refreshHelmetMissingIncidentsNow(filter);
  }
  onPageChange(pages, pageSizes) {
      let filter = this.state.reverseDirection.filter;

      filter.pages = pages;
      filter.pageSizes = pageSizes;
      this.refreshReverseDirectionIncidentsNow(filter);
    }

  onPageSizeChange(current, pageSize) {
    let filter = this.state.helmetMissing.filter;
    filter.pageSize = pageSize;
    this.refreshHelmetMissingIncidentsNow(filter);
  }
  onPageSizeChange(current, pageSizes) {
      let filter = this.state.reverseDirection.filter;
      filter.pageSizes = pageSizes;
      this.refreshReverseDirectionIncidentsNow(filter);
    }


  render() {

    return (
      <Tabs defaultActiveKey="1">
        <TabPane tab="Reverse" key="1">
          <div>
           {this.renderReverseData()}
          </div>
        </TabPane>
        <TabPane tab="Helmet-Missing" key="2">
          <div>
              {this.renderHelmetMissingData()}
          </div>
        </TabPane>
      </Tabs>
    );
  }

  renderReverseData(){
   if (this.state.reverseDirection.loading || (!this.state.reverseDirection.anprresponse.events))  {
          return <Empty description={false}/>
   }

   let events = this.state.reverseDirection.anprresponse.events;
   let count = this.state.reverseDirection.anprresponse.totalPages * this.state.reverseDirection.anprresponse.pageSizes;

   const paginationOption = {
       showSizeChanger: true,
       showQuickJumper: true,
       onShowSizeChange: this.onPageSizeChange,
       onChange: this.onPageChange,
       total: count
   };

   const pagination = {
       ...paginationOption,
       total: count,
       current: this.state.reverseDirection.filter.pages,
       pageSizes: this.state.reverseDirection.filter.pageSizes
   };

   return (
   <div>
   <Table dataSource={events} pagination={pagination}>


              <Column title="LPR" dataIndex="anprText" key="anprText"
                      render={anprText => anprText}/>
               <Column title="count" dataIndex="repeatedTimes" key="repeatedTimes"
                      render={repeatedTimes => repeatedTimes}/>
          </Table>
    </div>

   )
  }

  renderHelmetMissingData(){
   if (this.state.helmetMissing.loading || (!this.state.helmetMissing.anprresponse.events)) {
          return <Empty description={false}/>
   }

   let events = this.state.helmetMissing.anprresponse.events;
   let count = this.state.helmetMissing.anprresponse.totalPages * this.state.helmetMissing.anprresponse.pageSize;

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
       current: this.state.helmetMissing.filter.page,
       pageSize: this.state.helmetMissing.filter.pageSize
   };

   return (
   <div>
   <Table dataSource={events} pagination={pagination}>


              <Column title="LPR" dataIndex="anprText" key="anprText"
                      render={anprText => anprText}/>
               <Column title="count" dataIndex="repeatedTimes" key="repeatedTimes"
                      render={repeatedTimes => repeatedTimes}/>
          </Table>
    </div>

   )
  }
}
