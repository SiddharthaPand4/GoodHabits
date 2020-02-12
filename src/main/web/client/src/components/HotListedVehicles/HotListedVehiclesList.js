import React, {Component} from "react";
import {
    Card,
    Col,
    Icon,
    Pagination,
    Row,
    Modal,
    message, Input, Typography, Spin, Checkbox, Tag
} from 'antd';

import HotListVehicleService from "../../services/HotListVehicleService";

const {Search} = Input;
const {Text} = Typography;

export default class HotListedVehiclesList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activeModal: "",
            hotListedVehicleResponse: {},
            filter: {
                page: 1,
                pageSize: 40,
                lpr: ""
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
        this.search = this.search.bind(this);
        this.onLprInputChange = this.onLprInputChange.bind(this);

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

    search(searchText) {
        let {filter} = this.state;
        filter.lpr = searchText;
        this.setState({filter: filter});
        this.refresh(filter)
    }

    onLprInputChange(e) {

        let filter = this.state.filter;
        filter.lpr = e.target.value;
        this.setState({filter: filter})
    }

    render() {
        let {filter, hotListedVehicleResponse, workingVehicle, loading} = this.state;

        let vehicles = hotListedVehicleResponse.events;
        let count = hotListedVehicleResponse.totalPages * hotListedVehicleResponse.pageSize;


        if (loading.vehiclesList) {
            const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;
            return <Spin indicator={antIcon}/>
        }

        return (
            <div>
                <Row>
                    <Col xl={{span: 8}} lg={{span: 8}} md={{span: 16}} sm={{span: 12}} xs={{span: 12}}>
                        <Search allowClear placeholder="input search text" name="lpr" value={filter.lpr}
                                onChange={this.onLprInputChange} onSearch={value => this.search(value)} enterButton/>
                    </Col>
                </Row>

                <br/>
                <div>
                    <div>
                        <Row>
                            <Col xl={{span: 3}} lg={{span: 4}} md={{span: 6}} sm={{span: 8}} xs={{span: 16}}
                                 key={"new"}>
                                <Card style={{backgroundColor: "#40a9ff"}} hoverable
                                      onClick={() => this.openHotListVehicleForm(undefined)}>
                                    <div style={{textAlign: "center"}}>
                                        <Text style={{color: "white"}} strong>
                                            <Icon type="plus"/> New</Text>
                                    </div>
                                </Card>
                            </Col>
                            {(vehicles || []).map((vehicle, index) =>
                                <Col xl={{span: 3}} lg={{span: 4}} md={{span: 6}} sm={{span: 8}} xs={{span: 16}}
                                     key={index}>
                                    <div style={{textAlign: "center"}}>
                                        <Card hoverable onClick={() => this.openHotListVehicleForm(vehicle)}
                                              style={{backgroundColor: vehicle.archived ? "#fafafa" : ""}}>
                                            <div style={{textAlign: "center"}}>
                                                <Text delete={vehicle.archived} strong>
                                                    {vehicle.lpr}
                                                </Text>
                                            </div>
                                        </Card>
                                    </div>
                                </Col>
                            )}
                        </Row>

                        <Modal
                            title={<div><Tag>{workingVehicle.id ? "EDIT" : "NEW"}</Tag>{' '}<Text
                                copyable={workingVehicle.lpr.length > 0}>{workingVehicle.lpr}</Text></div>}
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
                    {(hotListedVehicleResponse && hotListedVehicleResponse.totalPages > 1) ?
                        <div style={{textAlign: "right"}}>
                            <Pagination onChange={this.onPageChange} onShowSizeChange={this.onPageSizeChange}
                                        showSizeChanger
                                        showQuickJumper
                                        defaultCurrent={1} total={count} current={this.state.filter.page}
                                        pageSize={this.state.filter.pageSize}/>
                        </div> : null}


                </div>

            </div>)
    }


}
