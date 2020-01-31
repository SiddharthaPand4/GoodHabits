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
import Moment from "react-moment";

import AnprService from "../../services/AnprService";

const {Paragraph, Text} = Typography;
const {Column} = Table;
export default class IncidentTimeline extends Component {

    constructor(props) {
        super(props);
        this.state = {

          loading:false,
          anprresponse: {},
          filter:{
            currentPage: 1,
            pageSizes: 24,
            lpr: "",
            incidentType:""
          }
        };

       this.toggleVisible = this.toggleVisible.bind(this);
       this.refreshBriefIncidentsNow = this.refreshBriefIncidentsNow.bind(this);
       this.onModalPageChange = this.onModalPageChange.bind(this);
       this.onModalPageSizeChange = this.onModalPageSizeChange.bind(this);
       this.refreshBriefIncidentsNow=this.refreshBriefIncidentsNow.bind(this);
    }

    componentWillReceiveProps(nextProps){
        let filter = this.state.filter;
        if((filter.lpr != nextProps.lpr) || (filter.incidentType != nextProps.incidentType)) {
            filter.lpr = nextProps.lpr;
            filter.incidentType = nextProps.incidentType;
            this.setState({filter},()=>{
              this.refreshBriefIncidentsNow();
            })

        }

    }

    toggleVisible(){
        let filter = this.state.filter;
        filter.currentPage = 1;
        this.setState({filter: filter});
        this.props.toggleVisible();

    }

    refreshBriefIncidentsNow() {

           let {loading, filter, anprresponse} = this.state;

           if(filter.lpr && filter.incidentType){
              this.setState({loading : true});
              AnprService.getIncidentTimeline(filter).then(request =>
              {
                loading = false;
                anprresponse= request.data;
                this.setState({loading, anprresponse});
              }).catch(error=> {
               loading = false;
               this.setState({loading});
               alert("Something went wrong");
                 });
           }

    }
      onModalPageChange(currentPage, pageSizes){
         let filter = this.state.filter;
         filter.currentPage = currentPage;
         filter.pageSizes = pageSizes;
         this.refreshBriefIncidentsNow(filter);
      }

      onModalPageSizeChange(current, pageSizes) {
           let filter = this.state.filter;
           filter.pageSizes = pageSizes;
           this.refreshBriefIncidentsNow(filter);
        }

    render(){
         let events = this.state.anprresponse.events;
         let count = this.state.anprresponse.totalPages * this.state.anprresponse.pageSizes;

         const paginationOption = {
                showSizeChanger: false,
                showQuickJumper: false,
                pageSize:5,
                showLessItems: true,
                onShowSizeChange: this.onModalPageSizeChange,
                onChange: this.onModalPageChange,
                total: count
            };

            const pagination = {
                ...paginationOption,
                total: count,
                current: this.state.filter.currentPage,
                pageSizes: this.state.filter.pageSizes
            };

         if (this.state.loading)
         {
            const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;
            return <Spin indicator={antIcon}/>
         }

         if (!this.state.anprresponse.events){
                   return <Empty description={false}/>
         }

        return <Modal

                title={<div><Paragraph copyable>{this.state.filter.lpr}</Paragraph></div> }
                visible={this.props.visible}
                onCancel={this.toggleVisible}
                onClose={this.toggleVisible}
                footer={[
                         <Button key="close"  type="primary" onClick={this.toggleVisible}>
                           Close
                         </Button>
                        ]}
                >
                   <div>
                              <Table dataSource={events} pagination={pagination}>
                                   <Column title="When and Where"


                                            render={(text, record, index)=> <Timeline.Item>
                                          <div>
                                            <p><Icon type="clock-circle" />  <Moment format="lll">{record.eventDate}</Moment></p>
                                            <p><Icon type="environment"/> {record.location}</p>
                                            <a title={"click here to download"}
                                              href={"/public/anpr/lpr/" + record.id + "/image.jpg"}
                                            download={true}>
                                            <img alt="event"
                                                src={"/public/anpr/lpr/" + record.id + "/image.jpg"}style={{width:160,height:"auto"}}/>
                                            </a>
                                          </div>
                                            </Timeline.Item>
                                            }/>
                                   <Column  title="Captured Image" dataIndex="id" key="anprimage"
                                             render={id => (
                                                     <a title={"click here to download"}  href={"/public/anpr/vehicle/" + id + "/image.jpg"}
                                                     download={true}>
                                                     <img alt="event"
                                                     src={"/public/anpr/vehicle/" + id + "/image.jpg" }style={{width:200,height:"auto"}}/></a>)}/>
                              </Table>
                           </div>

               </Modal>
    }
}