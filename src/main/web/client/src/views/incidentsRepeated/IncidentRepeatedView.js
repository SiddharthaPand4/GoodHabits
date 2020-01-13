import React, { Component } from "react"
import {
  Anchor,
  Card,
  Col,
  Collapse,
  Divider,
  Empty,
  Icon,
  Spin,
  Pagination,
  Row,
  Table,
  Tag,
  Timeline,
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
import GenericFilter from "../../components/GenericFilter";
import Moment from "react-moment";
import AnprService from "../../services/AnprService";
import IncidentTimeline from "./IncidentTimeline";
import Magnifier from "react-magnifier";

const { Link } = Anchor;
const { Panel } = Collapse;
const { Paragraph, Text } = Typography;
const {Column} = Table;
const { TabPane } = Tabs;
const { Search } = Input;
const antIcon = <Icon type="loading" style={{ fontSize: 24 }} spin />;
const tabList = [
    {
        key: 'Reverse',
        tab: 'Reverse',
    },
    {
        key: 'Helmet-Missing',
        tab: 'Helmet-Missing',
    },
];

export default class IncidentRepeatedView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      activeTab:"Reverse",
      visible:false,
      timelineLpr:"",
      filter: {
        lpr: ""
      },
      helmetMissing:{
        loading:false,
        anprresponse: {},
        filter: {
          pages: 1,
          pageSizes: 24,
          lpr: "",
          incidentType:""
        }
      },
      briefIncident:{
        loading:false,
        anprresponse: {},
          filter:{
            lpr: "",
          }
      },
      reverseDirection:{
        loading:false,
        anprresponse: {},
        filter: {
          pagess: 1,
          pageSizes: 24,
           lpr: "",
           incidentType:""
        }
      }
    };

    this.refresh = this.refresh.bind(this);
    this.handleRefresh = this.handleRefresh.bind(this);
    this.onHelmetMissingPageChange = this.onHelmetMissingPageChange.bind(this);
    this.onReverseDirectionPageChange = this.onReverseDirectionPageChange.bind(this);
    this.onHelmetMissingPageSizeChange = this.onHelmetMissingPageSizeChange.bind(this);
    this.onReverseDirectionPageSizeChange = this.onReverseDirectionPageSizeChange.bind(this);
    this.handleTabClick = this.handleTabClick.bind(this);
    this.refreshHelmetMissingIncidentsNow = this.refreshHelmetMissingIncidentsNow.bind(this);
    this.refreshReverseDirectionIncidentsNow = this.refreshReverseDirectionIncidentsNow.bind(this);
    this.onLprInputChange=this.onLprInputChange.bind(this);
    this.search=this.search.bind(this);
    this.onTabChange=this.onTabChange.bind(this);
    this.toggleIncidentTimelineModal = this.toggleIncidentTimelineModal.bind(this);
  }

  componentDidMount() {
    this.refresh();
  }

  handleTabClick(tabIndex) {
       this.setState({
         activeTabIndex:
          tabIndex === this.state.activeTabIndex
            ? this.props.defaultActiveTabIndex
          : tabIndex
       });
     }

  refresh() {
     this.refreshHelmetMissingIncidentsNow();
     this.refreshReverseDirectionIncidentsNow();
  }

  showModal = (lpr) => {
      this.setState({
        visible: true,
        timelineLpr: lpr
      });
  }

  toggleIncidentTimelineModal(){
    let visible = this.state.visible;
    this.setState({visible: !visible});
  }

  //cant use refresh to read from state as state may not have been set
  refreshHelmetMissingIncidentsNow() {

     let helmetMissing = this.state.helmetMissing;
     helmetMissing.loading = true;
     this.setState({helmetMissing:helmetMissing});

     AnprService.getHelmetMissingIncidentsRepeated(this.state.helmetMissing.filter).then(request =>
         {
           helmetMissing.loading = false;
           helmetMissing.anprresponse= request.data;
           this.setState({helmetMissing:helmetMissing});
         }).catch(error=> {
              helmetMissing.loading = false;
              this.setState({helmetMissing:helmetMissing});
              alert("Something went wrong");
            });
  }
  refreshReverseDirectionIncidentsNow() {
      let reverseDirection = this.state.reverseDirection;
      reverseDirection.loading=true;
      this.setState({reverseDirection:reverseDirection});
      AnprService.getReverseDirectionIncidentsRepeated(this.state.reverseDirection.filter).then(request =>
          {
           reverseDirection.loading = false;
           reverseDirection.anprresponse= request.data;
           this.setState({reverseDirection:reverseDirection});
          }).catch(error=> {
             reverseDirection.loading = false;
             this.setState({ hasError: true });
             alert("Something went wrong");
          });
  }


  handleRefresh(){
    this.refresh();
  }

  onLprInputChange(e) {
    let filter = this.state.filter;
    filter.lpr = e.target.value;
    this.setState({filter: filter})
  }

  search(searchText) {
    let {filter, reverseDirection, helmetMissing} = this.state;
    filter.lpr = searchText;
    reverseDirection.filter.lpr = searchText;
    helmetMissing.filter.lpr = searchText;
    this.setState({filter,reverseDirection,helmetMissing},()=>{
      this.refresh();
    });
  }

  onHelmetMissingPageChange(page, pageSize){
    let filter = this.state.helmetMissing.filter;
    filter.page = page;
    filter.pageSize = pageSize;
    this.refreshHelmetMissingIncidentsNow(filter);
  }

  onReverseDirectionPageChange(pages, pageSizes){
     let filter = this.state.reverseDirection.filter;
     filter.pages = pages;
     filter.pageSizes = pageSizes;
     this.refreshReverseDirectionIncidentsNow(filter);
  }

  onHelmetMissingPageSizeChange(current, pageSize){
    let filter = this.state.helmetMissing.filter;
    filter.pageSize = pageSize;
    this.refreshHelmetMissingIncidentsNow(filter);
  }

  onReverseDirectionPageSizeChange(current, pageSizes) {
     let filter = this.state.reverseDirection.filter;
     filter.pageSizes = pageSizes;
     this.refreshReverseDirectionIncidentsNow(filter);
  }

  onTabChange(key) {
      this.setState({activeTab: key})
  }

  render() {

    return (
          <div>
            <div>

            </div>

                <Card
                style={{width: '100%'}}
                title={<Row>
                  <Col xl={{span: 16}} lg={{span: 16}} md={{span: 12}} sm={{span: 12}} xs={{span: 12}}>
                       <h4>Repeated Incidents</h4>
                  </Col>
                  <Col xl={{span: 8}} lg={{span: 8}} md={{span: 12}} sm={{span: 12}} xs={{span: 12}}>

                     <Search  allowClear
                       placeholder="Search Vehicle "
                       onChange={this.onLprInputChange}
                       style={{textAlign:"right"}}
                       onSearch={value => this.search(value)} enterButton
                      />
                  </Col>
                </Row>}

                tabList={tabList}
                activeTabKey={this.state.activeTab}

                onTabChange={key => {
                    this.onTabChange(key);
                }}
                >
                 {this.state.activeTab === "Reverse" ? (this.renderReverseData()) : this.renderHelmetMissingData()}

                 <IncidentTimeline
                   lpr={this.state.timelineLpr}
                   incidentType={this.state.activeTab}
                   visible={this.state.visible}
                   toggleVisible={this.toggleIncidentTimelineModal}
                   />
              </Card>

          </div>
    );
  }

  renderReverseData(){


   let events = this.state.reverseDirection.anprresponse.events;
   let count = this.state.reverseDirection.anprresponse.totalPages * this.state.reverseDirection.anprresponse.pageSizes;

   const paginationOption = {
       showSizeChanger: true,
       showQuickJumper: true,
       onShowSizeChange: this.onReverseDirectionPageSizeChange,
       onChange: this.onReverseDirectionPageChange,
       total: count
   };

   const pagination = {
       ...paginationOption,
       total: count,
       current: this.state.reverseDirection.filter.pages,
       pageSizes: this.state.reverseDirection.filter.pageSizes
   };

   if (this.state.reverseDirection.loading)
   {
      const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;
      return <Spin indicator={antIcon}/>
   }

   if (!this.state.reverseDirection.anprresponse.events){
       return <Empty description={false}/>
   }

   return (
     <div>
       <Table dataSource={events} pagination={pagination}>

         <Column  title="LPR" dataIndex="anprText" key="anprText"
            render={anprText => <Button onClick={()=> this.showModal(anprText)}>{anprText}</Button>}/>

         <Column title="Repeated Times" dataIndex="repeatedTimes" key="repeatedTimes"
             render={repeatedTimes => repeatedTimes}/>
       </Table>
     </div>

   )
  }

  renderHelmetMissingData(){


   let events = this.state.helmetMissing.anprresponse.events;
   let count = this.state.helmetMissing.anprresponse.totalPages * this.state.helmetMissing.anprresponse.pageSize;

   const paginationOptions = {
       showSizeChanger: true,
       showQuickJumper: true,
       onShowSizeChange: this.onHelmetMissingPageSizeChange,
       onChange: this.onHelmetMissingPageChange,
       total: count
   };

   const pagination = {
       ...paginationOptions,
       total: count,
       current: this.state.helmetMissing.filter.page,
       pageSize: this.state.helmetMissing.filter.pageSize
   };

   if (this.state.helmetMissing.loading)
   {
      const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;
      return <Spin indicator={antIcon}/>
   }

   if (!this.state.helmetMissing.anprresponse.events){
             return <Empty description={false}/>
   }

    return (
      <div>
         <Table dataSource={events} pagination={pagination}>

              <Column title="LPR" dataIndex="anprText" key="anprText"
                      render={anprText => <Button onClick={()=> this.showModal(anprText)}>{anprText}</Button>}/>
               <Column title="Repeated Times" dataIndex="repeatedTimes" key="repeatedTimes"
                      render={repeatedTimes => repeatedTimes}/>
         </Table>
      </div>

    )
  }
}
