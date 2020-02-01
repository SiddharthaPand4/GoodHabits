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
import AnprService from "../../services/AnprService";
import Magnifier from "react-magnifier";

const {Paragraph, Text} = Typography;

const {Column} = Table;
const {Panel} = Collapse;
const { Search } = Input;


export default class ParkingInOutView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            visible:true,
            loading: true,
            layout: "list",
            events: {},
            filter: {
                page: 1,
                pageSize: 12
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


    }









    render() {

        let layout = this.state.layout;
        let lpr = this.state.filter.lpr;

        return (
            <div>

                <div>
                    Division for upper view
                   <Row>

                     <Col span={12}>Column for video link

                        <iframe width="440" height="260" src="https://www.youtube.com/embed/lpp7DqsBXbg" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                     </Col>

                     <Col span={12}>
                            Column For Vehicle No.
                            <Search
                                  placeholder="Enter Vehicle No."
                                  enterButton="Search"
                                  size="large"
                                  onSearch={value => console.log(value)}
                            />
                     </Col>
                   </Row>
                </div>

               <div>
                 <Row>
                   <Col span={11}>
                     <Button type="primary"size="large" block="false">
                       IN
                     </Button>
                   </Col>
                   <Col span={2}>
                   </Col>
                   <Col span={11}>
                     <Button type="primary"size="large" block="false">
                       OUT
                     </Button>
                   </Col>
                 </Row>
               </div>

               <div>
                  Division for table
                  <Table >
                         <Column  title="S.No." />
                         <Column  title="Vehicle No." />
                         <Column  title="In-TimeStamp" />
                         <Column  title="Out-TimeStamp" />
                         <Column  title="TimeStamp Diff." />



                  </Table>
               </div>
            </div>)
    }

}


