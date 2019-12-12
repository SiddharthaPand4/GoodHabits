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
    message, Input, Button, Menu, Dropdown, Typography, Slider, Popconfirm, Spin, Checkbox
} from 'antd';

import HotListVehicleService from "../../services/HotListVehicleService";


const {Column} = Table;
const {Panel} = Collapse;
const {Paragraph, Text} = Typography;


export default class HotListedVehiclesList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activeModal: "",
            hotListedVehicleResponse: {},
            filter: {
                page: 1,
                pageSize: 40
            },
            workingVehicle: {
                id: undefined,
                lpr: "",
                archived: false
            },
            loading: {
                vehiclesList: false
            }
        };

        this.refresh = this.refresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.openHotListVehicleForm = this.openHotListVehicleForm.bind(this);
        this.handleSubmitWorkingHotListVehicle = this.handleSubmitWorkingHotListVehicle.bind(this);
        this.handleCancelWorkingHotListVehicle = this.handleCancelWorkingHotListVehicle.bind(this);
        this.archiveHotListedVehicle = this.archiveHotListedVehicle.bind(this);
        this.workingVehicleOnChange = this.workingVehicleOnChange.bind(this);
    }

    componentDidMount() {
        this.refresh(this.state.filter);
    }

    refresh(filter) {
        let loading = this.state.loading;
        loading.vehiclesList = true;
        this.setState({loading});
        HotListVehicleService.getHotListedVehicles(filter).then(response => {
            loading.vehiclesList = false;
            this.setState({hotListedVehicleResponse: response.data, loading})
        }).catch(error => {
            loading.vehiclesList = false;
            this.setState({loading});
            message.error('Something went wrong!');
        })
    }


    archiveHotListedVehicle(event) {
        HotListVehicleService.archiveHotListedVehicle(event).then(request => {
            this.refresh(this.state.filter);
            message.success('Vehicle archived!');
        }).catch(error => {
            message.error('Something went wrong!');
        })
    }


    onPageChange(page, pageSize) {
        let filter = this.state.filter;
        filter.page = page;
        filter.pageSize = pageSize;
        this.refresh(filter)
    }

    onPageSizeChange(current, pageSize) {
        let filter = this.state.filter;
        filter.pageSize = pageSize;
        this.refresh(filter);
    }


    confirm(e) {
        console.log(e);
        message.success('Click on Yes');
    }

    cancel(e) {
        console.log(e);
        message.error('Click on No');
    }

    openHotListVehicleForm(vehicle) {
        let workingVehicle = {
            id: undefined,
            lpr: "",
            archived: false
        };
        if (vehicle) {
            workingVehicle.id = vehicle.id;
            workingVehicle.lpr = vehicle.lpr;
            workingVehicle.archived = vehicle.archived;
        }

        this.setState({
            activeModal: "workingVehicle", workingVehicle: workingVehicle
        });
    };

    workingVehicleOnChange(e) {
        let {workingVehicle} = this.state;

        switch (e.target.type) {
            case "checkbox":
                workingVehicle[e.target.name] = e.target.checked;
                break;
            default:
                workingVehicle[e.target.name] = e.target.value;
                break;

        }
        this.setState({workingVehicle: workingVehicle});
    }

    handleSubmitWorkingHotListVehicle(e) {
        HotListVehicleService.saveHotListedVehicle(this.state.workingVehicle).then(response => {
            this.refresh(this.state.filter);
            let workingVehicle = {
                id: undefined,
                lpr: "",
                archived: false
            };
            this.setState({activeModal: "", workingVehicle});
            message.success('Saved!');
        }).catch(error => {
            message.error('Something went wrong!');
        })
    };

    handleCancelWorkingHotListVehicle(e) {
        let workingVehicle = {
            id: undefined,
            lpr: "",
            archived: false
        };
        this.setState({
            activeModal: "",
            workingVehicle
        });
    };


    render() {
        let vehicles = this.state.hotListedVehicleResponse.events;
        let workingVehicle = this.state.workingVehicle;

        let count = this.state.hotListedVehicleResponse.totalPages * this.state.hotListedVehicleResponse.pageSize;

        if (this.state.loading.vehiclesList) {

            const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;

            return <Spin indicator={antIcon}/>
        }

        if (!this.state.hotListedVehicleResponse || this.state.hotListedVehicleResponse.totalPage === 0) {
            return <Empty description={false}/>
        }


        return (
            <div>
                <div>
                    <Row>
                        <Col xl={{span: 3}} lg={{span: 4}} md={{span: 6}} sm={{span: 8}} xs={{span: 16}} key={"new"}>
                            <Card style={{backgroundColor: "#40a9ff"}} hoverable
                                  onClick={() => this.openHotListVehicleForm(undefined)}>
                                <div style={{textAlign: "center"}}>
                                    <Text style={{color: "white"}} strong>
                                        <Icon type="plus"/> Add Vehicle</Text>
                                </div>
                            </Card>
                        </Col>
                        {(vehicles || []).map((vehicle, index) =>
                            <Col xl={{span: 3}} lg={{span: 4}} md={{span: 6}} sm={{span: 8}} xs={{span: 16}}
                                 key={index}>
                                <Card hoverable onClick={() => this.openHotListVehicleForm(vehicle)}>
                                    <div style={{textAlign: "center"}}>
                                        <Text delete={vehicle.archived} strong>
                                            {vehicle.lpr}
                                        </Text>
                                    </div>
                                </Card>
                            </Col>
                        )}
                    </Row>

                    <Modal
                        title="Vehicle"
                        visible={this.state.activeModal === "workingVehicle"}
                        onOk={this.handleSubmitWorkingHotListVehicle}
                        onCancel={this.handleCancelWorkingHotListVehicle}
                    >
                        <Checkbox onChange={this.workingVehicleOnChange} name={"archived"}
                                  checked={workingVehicle.archived}>Archive</Checkbox>
                        <br/>
                        <br/>
                        <Input onChange={this.workingVehicleOnChange} name={"lpr"} value={workingVehicle.lpr}
                               size="large"
                               placeholder="Enter vehicle number here"/>


                    </Modal>


                </div>

                <div style={{textAlign: "right"}}>
                    <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange} showSizeChanger
                                showQuickJumper
                                defaultCurrent={1} total={count} current={this.state.filter.page}
                                pageSize={this.state.filter.pageSize}/>
                </div>

            </div>)
    }


}
