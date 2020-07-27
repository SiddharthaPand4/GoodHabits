import React from "react"
import {Component} from "react";
import {
    Button,
    Card,
    Col,
    Divider,
    Dropdown,
    Form, Icon,
    Input,
    Menu,
    message,
    Modal,
    notification,
    Row,
    Table,
    Tag, Typography
} from "antd";
import '../form.css';
import RoleService from "../services/RoleService";
import UserService from "../services/UserService";
import Column from "antd/lib/table/Column";
import {EventBus} from "../components/event";

const {Text} = Typography;
const {confirm} = Modal;
let flag = false;
export default class RoleView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            formVisible: false,
            roles: [],
            mode: "Add",
            role: {id: "", name: "", privileges: []},
            privileges: []
        }
        EventBus.subscribe('role-refresh', (event) => this.refresh())
        this.close = this.close.bind(this);
        this.addNewRole = this.addNewRole.bind(this);
        this.showDeleteConfirm = this.showDeleteConfirm.bind(this);
        this.refresh = this.refresh.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        UserService.getRoles().then(request => {
            this.setState({roles: request.data, loading: false})
        })
    }

    refreshNow() {
        this.refresh();
    }

    close() {
        this.setState({"formVisible": false})
    }

    showDeleteConfirm(roleId, refresh) {
        confirm({
            title: 'Are you sure you want to Delete this Role',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk() {
                RoleService.removeRole(roleId)
                    .then(() => {
                        refresh();
                        message.success("Deleted Successfully!")
                    }).catch(error => {
                    let msg = "Something went wrong!";
                    if (error && error.response && error.response.data && error.response.data.message) {
                        msg = error.response.data.message;
                        message.warn(msg);
                    }

                });
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    addNewRole() {
        this.setState({formVisible: true, role: {name: "", privileges: []}, mode: "Add"})
        flag = false;
    }

    editRole(RoleId) {
        this.setState({"formVisible": true, "mode": "Edit"})
        flag = true;
        RoleService.getRole(RoleId)
            .then(response => {
                    this.setState({role: response.data})
                },
                error => {
                    message.error(error.response.data.message);
                });
    }

    render() {
        let roles = this.state.roles
        const WrappedRoleForm = Form.create({name: 'role_form'})(AddRoleForm);
        const formVisible = this.state.formVisible;
        if (this.state.loading || !this.state.roles || this.state.roles.length === 0) {
            roles = []
        }
        return (
            <div>
                <Row gutter={2}>
                    <Col span={2}>
                        <Button type="primary" onClick={this.addNewRole}>
                            <Icon type="plus-circle"/> New Role
                        </Button>
                    </Col>
                </Row>
                <br/>
                <Row gutter={24}>
                    <Col span={16}>
                        <Card
                            className="limitable"
                            bodyStyle={{padding: "0px", width: "100%"}}
                        >
                            <Table dataSource={this.state.roles} pagination={false} scroll={{x: true}}>
                                <Column align="center" title="Role" dataIndex="name" key="name"
                                        render={(name) => name}/>
                                <Column title="Privileges" dataIndex="privileges"
                                        render={privileges => (
                                            <>
                                                {privileges.map(privilege => {
                                                    return (
                                                        <Tag color="blue" key={privilege}>
                                                            {privilege}
                                                        </Tag>
                                                    );
                                                })}
                                            </>
                                        )
                                        }/>
                                <Column title="Actions" render={(record) => (
                                    <div>
                                        <Icon type="edit" onClick={() => this.editRole(record.id)}/>
                                        <Divider type="vertical"/>
                                        <Icon type="delete" style={{color: "#ff0000"}}
                                              onClick={() => this.showDeleteConfirm(record.id, this.refresh)}/>
                                    </div>
                                )}/>

                            </Table>
                        </Card>
                    </Col>


                    {formVisible ?
                        <Col span={8}>
                            {this.state.mode === "Add" ?
                                <Card title="Add Role">
                                    <WrappedRoleForm role={this.state.role} close={this.close}/>
                                </Card>
                                :
                                <Card title="Edit Role">
                                    <WrappedRoleForm role={this.state.role} close={this.close}/>

                                </Card>
                            }
                        </Col>
                        : null
                    }
                </Row>
            </div>)
    }


}

class AddRoleForm extends Component {
    constructor(props) {
        super(props);
        this.state = {
            role: {name: "", privileges: []},
            PrivilegeOptions: [],
            PrivilegeChoice: this.props.role.privileges,
            submitted: false,
            loading: false
        }
        this.getAllPrivileges = this.getAllPrivileges.bind(this);
        this.handlePrivilegeMenuClick = this.handlePrivilegeMenuClick.bind(this);
        this.removePrivilegeChoice = this.removePrivilegeChoice.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.close = this.close.bind(this);
    }

    componentDidMount() {
        this.getAllPrivileges();
    }

    handleSubmit(e) {

        e.preventDefault();
        const form = this.props.form;
        var role = {};
        role.name = form.getFieldValue("name");
        role.privileges = this.state.PrivilegeChoice;
        role.id = this.props.role.id;
        let validationError;
        if (!role.name) {
            validationError = "Name missing"
        }

        if (role.privileges.length === 0) {
            validationError = "Privileges not assigned to role"
        }
        if (validationError) {
            this.setState({validationError: validationError});
            return
        }

        console.log('saving role', role);
        this.setState({submitted: true, loading: true});

        RoleService.addRole(role, flag).then(response => {
            if (flag) {
                message.success("Role updated")
                this.close()
            } else {
                message.success("Role Added")
            }
            EventBus.publish('role-refresh', {})

        }).catch(error => {
            let msg = "Something went wrong!";
            if (error && error.response && error.response.data && error.response.data.message) {
                msg = error.response.data.message;
                message.warn(msg);
            }

        });
    }

    close() {
        this.props.close();
    }

    render() {
        const {getFieldDecorator, getFieldsError} = this.props.form;
        const PrivilegeChoice = this.state.PrivilegeChoice;
        const validationError = this.state.validationError;
        const PrivilegeMenu = (
            <Menu onClick={this.handlePrivilegeMenuClick}>
                {(this.state.PrivilegeOptions || []).map((privilege) =>
                    <Menu.Item key={privilege.id}>
                        {privilege.name}
                    </Menu.Item>
                )}
            </Menu>
        );
        return (
            <div>
                <Form onSubmit={this.handleSubmit}>
                    <Form.Item label="Role" className="formitem">
                        {getFieldDecorator('name', {
                            initialValue: this.props.role.name,
                            rules: [{required: true, message: 'Please input Role Name!'}],
                        })(
                            <Input
                                type="text"
                                placeholder="Role Name"/>
                        )}
                    </Form.Item>
                    <Form.Item label="Privileges" className="formitem">
                        {getFieldDecorator('privileges', {
                            rules: [{required: true, message: 'Add Privileges as per Role'}],
                        })(
                            <Dropdown overlay={PrivilegeMenu}>
                                <Button>
                                    Add Privileges <Icon type="down"/>

                                </Button>
                            </Dropdown>
                        )}
                        <br/>
                        {
                            PrivilegeChoice.map((privilege, index) => {
                                    return (
                                        <Tag color="blue"
                                             key={privilege} closable
                                             onClose={() => this.removePrivilegeChoice(index)}>
                                            {privilege}
                                        </Tag>)

                                }
                            )

                        }

                    </Form.Item>
                    <br/><br/>
                    <div>
                        <Button type="primary" htmlType="submit" disabled={this.hasErrors(getFieldsError())}>
                            Save
                        </Button>
                        <span>&nbsp;&nbsp;</span>
                        <Button type="secondary" className="user-form-button" onClick={this.close}>
                            Close
                        </Button>
                        <br/>{validationError && <Text type="danger">{validationError}</Text>}
                    </div>

                </Form>

            </div>
        );
    }

    hasErrors(fieldsError) {
        return Object.keys(fieldsError).some(field => fieldsError[field]);
    }

    search(privilegeId, Array) {
        for (let i = 0; i < Array.length; i++) {
            if (Array[i].id === privilegeId) {
                return true;
            }
        }
    }

    handlePrivilegeMenuClick(choice) {

        if (!this.state.PrivilegeChoice.includes(choice.item.props.children, 0)) {
            this.setState(prevState => ({
                PrivilegeChoice: [...prevState.PrivilegeChoice, choice.item.props.children]
            }))
        }
    }


    getAllPrivileges() {
        RoleService.getAllPrivilegeTypes().then(response => {
            this.setState({PrivilegeOptions: response.data});
        }).catch(error => {
            notification.open({
                message: 'Something went wrong ',
                discription: error
            });
        })
    }

    removePrivilegeChoice(index) {
        const PrivilegeChoice = Object.assign([], this.state.PrivilegeChoice)
        PrivilegeChoice.splice(index, 1)
        this.setState({PrivilegeChoice: PrivilegeChoice})
    }
}