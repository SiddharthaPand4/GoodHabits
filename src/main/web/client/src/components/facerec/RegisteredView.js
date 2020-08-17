import React, {Component} from "react";
import {Button, Card, Col, Collapse, Empty, Form, Input, Row, Select, Spin, Tag} from "antd";
import FaceMatchService from "../../services/facerec/FrsService";
import GenericFilter from "../GenericFilter";

const {Panel} = Collapse;

export default class RegisteredView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activePanelKey: ["1"],
            loading: true,
            users:[],
            filter: {
                page: 1,
                pageSize: 12,
                name: "",
                type: "",
                accessType: ""
            },
        }
        this.refresh = this.refresh.bind(this);
        this.handleFilterChange = this.handleFilterChange.bind(this);
        this.handleLayoutChange = this.handleLayoutChange.bind(this);
        this.handleRefresh = this.handleRefresh.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.onNameInputChange = this.onNameInputChange.bind(this);
        this.handleTypeChange = this.handleTypeChange.bind(this);
        this.handleAccessTypeChange = this.handleAccessTypeChange.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        this.setState({loading: true});
        FaceMatchService.getRegisteredUsers(this.state.filter).then(response => {
            this.setState({"userresponse": response.data, loading: false})
        }).catch(error => {
            alert("Something went wrong!");
            this.setState({loading: false});
        })
    }

    switchWhiteBlack(user) {
        FaceMatchService.toggleWhiteList(user.uid)
        .then( () => {
            this.refresh();
        })
    }

    onNameInputChange(e) {

        let filter = this.state.filter;
        filter.name = e.target.value;
        this.setState({filter: filter})
    }

    handleTypeChange(value) {
        let filter = this.state.filter;
        filter.type = value;
        this.setState({filter:filter})
    }

    handleAccessTypeChange(value) {
        let filter = this.state.filter;
        filter.accessType = value;
        this.setState({filter:filter})
    }

    handleFilterChange(data) {
        this.setState({filter: data})
    }

    handleLayoutChange(data) {
        this.setState({layout: data})
    }

    handleRefresh() {
        this.refresh();
    }

    onPageChange(page, pageSize) {
        let filter = this.state.filter;
        filter.page = page;
        filter.pageSize = pageSize;
        this.setState({filter}, () => {
            this.refresh();
        });
    }

    onPageSizeChange(current, pageSize) {
        let filter = this.state.filter;
        filter.pageSize = pageSize;
        this.setState({filter}, () => {
            this.refresh();
        });
    }

    render() {

        let layout = this.state.layout;
        let filtername = this.state.filter.name;

        return (<div>
            <h3>Registered Users</h3>
            <div>
                <Collapse>
                    <Panel header="Filter" key="1">
                        <div>      Name: <Input value={filtername} style={{"width": "200px"}}
                                               onChange={this.onNameInputChange}/>&nbsp;&nbsp;

                            Type: <Select defaultValue="" onChange={this.handleTypeChange}>
                                <Select.Option value="">--Select--</Select.Option>
                                <Select.Option value="Employee">Employee</Select.Option>
                                <Select.Option value="Vendor">Vendor</Select.Option>
                                <Select.Option value="Visitor">Visitor</Select.Option>
                                <Select.Option value="Others">Others</Select.Option>
                            </Select>

                            Access Type: <Select defaultValue="" onChange={this.handleAccessTypeChange}>
                                <Select.Option value="">--Select--</Select.Option>
                                <Select.Option value="BlackList">BlackList</Select.Option>
                                <Select.Option value="WhiteList">WhiteList</Select.Option>
                            </Select>
                        </div>
                        <GenericFilter handleRefresh={this.refresh} filter={this.state.filter} layout={layout}
                                       handleFilterChange={this.handleFilterChange}
                                       handleLayoutChange={this.handleLayoutChange}
                        />
                    </Panel>
                </Collapse>
                <div>
                    <Spin spinning={this.state.loading}>
                        {this.renderGrid()}
                    </Spin>
                </div>
            </div>
        </div>)
    }

    renderGrid() {
        if (this.state.loading || !this.state.userresponse || this.state.userresponse.totalPages === 0) {
            return <Empty description={false}/>
        }

        let users = this.state.userresponse.users;

        return <div style={{background: '#ECECEC', padding: '5px'}}>
            <Row>
                {
                    users.map((user,index) =>
                        <Col xl={{span: 8}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}} key={index}>
                            <Card
                                style={{margin: "5px"}}
                                title={
                                    <div>
                                        {(user.type) ? <Tag color="#f50">{user.type}</Tag> : null}
                                        {(user.accessType) ? <Tag color="#f50">{user.accessType}</Tag> : null}
                                        <h4>{user.pid}</h4>
                                        <h5>{user.name}</h5>
                                    </div>
                                }
                                >

                                <div style={{textAlign: "center"}}>
                                    <img alt="face" style={{width:100,height:100, borderRadius:"50%"}}
                                         src={"/public/frs/person/face/" + user.uid + "/image.jpg"}/>
                                    <br/>
                                    <img alt="person" style={{width:200,height:200}}
                                         src={"/public/frs/person/full/" + user.uid + "/image.jpg"}/>
                                </div>
                                <div style={{textAlign: "center"}}>
                                    <Button onClick={() => this.switchWhiteBlack(user)}>{(user.accessType === "WhiteList") ? "BlackList this Person" : "WhiteList this Person"}</Button>
                                </div>
                                </Card>
                        </Col>
                            )
                }
            </Row>
        </div>
    }


}
