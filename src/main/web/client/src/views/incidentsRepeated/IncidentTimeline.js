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


const {Column} = Table;
export default class IncidentTimeline extends Component {

    constructor(props) {
        super(props);
        this.state = {

          loading:false,
          anprresponse: {},
          filter:{
            lpr: "",
            incidentType:""
          }
        };

       this.toggleVisible = this.toggleVisible.bind(this);
       this.refreshBriefIncidentsNow = this.refreshBriefIncidentsNow.bind(this);
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
        let visible = this.state.visible;
        this.setState({visible: !visible});

    }

    refreshBriefIncidentsNow() {

           let {loading, filter, anprresponse} = this.state;

           if(filter.lpr && filter.incidentType){
              this.setState({loading : true});
              AnprService.getBriefIncidentsRepeated(filter).then(request =>
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

    render(){
         let events = this.state.anprresponse.events;

         if (this.state.loading)
         {
            const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;
            return <Spin indicator={antIcon}/>
         }

         if (!this.state.anprresponse.events){
                   return <Empty description={false}/>
         }

        return <Modal

                title={<div>{this.state.filter.incidentType}&nbsp;&nbsp;|&nbsp;&nbsp;{this.state.filter.lpr}</div>}
                visible={this.props.visible}
                onCancel={this.props.toggleVisible}
                onClose={this.props.toggleVisible}
                footer={[
                         <Button key="close"  type="primary" onClick={this.props.toggleVisible}>
                           Close
                         </Button>
                        ]}
                >
                 <div >
                   <Row >

                      {
                          (events || []).map((record, index)=>{

                          return  <Timeline.Item><Row gutter={8} >
                                <Col span={12}  >
                                  <div ><p><Icon type="clock-circle" />  <Moment format="lll">{record.eventDate}</Moment></p>
                                             <p><Icon type="environment"/> {record.location}</p>
                                             <a title={"click here to download"}
                                               href={"/public/anpr/lpr/" + record.id + "/image.jpg"}
                                             download={true}>
                                             <img alt="event"
                                                 src={"/public/anpr/lpr/" + record.id + "/image.jpg"}style={{width:160,height:"auto"}}/>
                                             </a></div>
                                </Col>
                                <Col span={12}>
                                  <div> <a title={"click here to download"}
                                           href={"/public/anpr/vehicle/" + record.id + "/image.jpg"}
                                                     download={true}>
                                                         <img alt="event"
                                                         src={"/public/anpr/vehicle/" + record.id + "/image.jpg" } class="responsive" style={{maxWidth:"160px",height:"auto"}}/>
                                               </a></div>
                                </Col>
                            </Row><Divider /></Timeline.Item>



                          })
                      }

                   </Row>
                 </div>
               </Modal>
    }
}