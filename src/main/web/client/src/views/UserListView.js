import React, {Component} from "react";
import {Table, Divider, Row, Col, Card, Form, Button, Input, Icon, Typography, Select, Modal, message, Tag} from 'antd';
import UserService from "../services/UserService";

import '../form.css';
const { Column} = Table;
const {Text} = Typography;
const { confirm } = Modal;
export default class UserListView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            users: [],
            showUserDetails:false,
            mode:"Add",
            user:{userName:"", lastName:"",firstName:"",email:"",password:"",roles:[],id:""},
            roles:[],
            visible: false ,
        };

        this.addUser=this.addUser.bind(this);
        this.refresh=this.refresh.bind(this);
        this.refreshUsers=this.refreshUsers.bind(this);
        this.close=this.close.bind(this);

    }

    componentDidMount() {
        this.refresh();
    }

    refresh(){
        UserService.getUsers().then(request => {
            this.setState({"users": request.data, loading: false,showUserDetails:false})
        },
        error=>{
          message.error(error.response.data.message);
        });

        UserService.getRoles().then(request =>{
            this.setState({roles:request.data})
        },
        error=>{
          message.error(error.response.data.message);
        });
    }

    refreshUsers(){
        this.refresh();
    }

    addUser(){
        this.setState({mode:"Add",showUserDetails:true, user:{userName:"", lastName:"",firstName:"",email:"",password:""}})
    }

    showUser(userId){
        this.setState({showUserDetails:true,mode:"Edit"});
        UserService.getUser(userId).then(response =>{
            this.setState({user : response.data})
        },
        error=>{
          message.error(error.response.data.message);
        });
    }

    close(){
        this.setState({showUserDetails:false})
    }

     showDeleteConfirm(userId,refresh) {
      confirm({
        title: 'Are you sure you want to make user inactive?',
        okText: 'Yes',
        okType: 'danger',
        cancelText: 'No',
        onOk() {
          console.log('OK');
          UserService.deleteUser(userId).then(response=>{
            message.success("User Deactivated!");
            refresh();
          },
          error=>{
            message.error(error.response.data.message);
          });


        },
        onCancel() {
          console.log('Cancel');
        },
      });
     }

    render() {

     const WrappedUserForm = Form.create({name: 'user_form'})(UserForm);
     const showUserDetails=this.state.showUserDetails;

        return (
         <div>
            <Row gutter={2}>
                <Col span={2}>
                    <Button type="primary" onClick={this.addUser}>
                        <Icon type="plus-circle" /> New User
                    </Button>
                </Col>
            </Row>
            <br/>
            <Row gutter={24}>
                <Col span={16}>
                    <Card
                      className="limitable"
                      bodyStyle={{ padding: "0px", width: "100%" }}
                    >
                     <Table dataSource={this.state.users} pagination={false} scroll={{ x: true}} >
                            <Column title="Username" dataIndex="userName" key="userName" render={(text, record) => (
                                <span>{text}</span>
                            )}/>
                            <Column title="Email" dataIndex="email" key="email" />
                            <Column title="Role" dataIndex="roles" render={roles => (
                                <>
                                    {roles.map(role => {
                                        return (
                                            <Tag color="blue" key={role} >
                                                {role}
                                            </Tag>

                                        );
                                    })}
                                </>
                            )
                            }/>

                            <Column title="Action" key="action" render={(text, record) => (
                                    <span>
                                          <Icon type="edit" onClick={()=>this.showUser(record.id)}/>
                                        <Divider type="vertical" />
                                        <Icon type="delete" style={{color: "#ff0000"}} onClick={()=>this.showDeleteConfirm(record.id,this.refresh)} />
                                    </span>
                                )}
                            />
                     </Table>
                    </Card>
                </Col>

               {showUserDetails ?
                 <Col span={8}>
                   <WrappedUserForm user={this.state.user} roles={this.state.roles} refresh={this.refreshUsers} close={this.close} mode={this.state.mode}/>
                 </Col>
                :null}

            </Row>
         </div>
        )
    }
}

class UserForm extends Component {
    constructor(props) {
        super(props);

        this.state = {
            user:{},
            loading: false
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.close = this.close.bind(this);

    }

    close(){
        this.props.close();
    }

    handleSubmit(e) {
        e.preventDefault();
        const form = this.props.form;
        let user = {};
        user.firstName = form.getFieldValue("firstname");
        user.userName = form.getFieldValue("username");
        user.lastName = form.getFieldValue("lastname");
        user.email = form.getFieldValue("email");
        user.password = form.getFieldValue("password");
        user.roles=form.getFieldValue("role");
        user.id=this.props.user.id;
        let validationError;
        if (!user.firstName) {
            validationError = "Missing firstname"
        }
        if (!user.userName) {
            validationError = "Missing username"
        }
        if (!user.lastName) {
            validationError = "Missing lastname"
        }
        if (!user.email) {
            validationError = "Missing email"
        }

        if(user.roles.length===0){
            validationError="Missing role"
        }
        if (validationError) {
            this.setState({validationError: validationError});
            return
        }
        this.setState({loading: true});
        console.log('saving user', user);
        UserService.createUser(user).then(response => {
            if(user.id){
                 message.success("User updated")
            }
            else{
                 message.success("User created")
            }
            this.setState({loading: false});
            this.props.refresh();

        }).catch(error=> {
            console.log(error);
            this.setState({loading: false});
        });
    }



    render() {

        const {getFieldDecorator} = this.props.form;
        const validationError = this.state.validationError;

         return (
            <Card title={this.props.mode ==="Add"? "Add User" : "Edit User"}>

               <Form onSubmit={this.handleSubmit} className="user-form" >
                    <Form.Item label="Username" className="formitem">
                        {getFieldDecorator('username', {
                            initialValue:this.props.user.userName,
                            rules: [{required: true, message: 'Please input your username!'}],
                        })(
                            <Input
                                type="text"
                                placeholder="Username"

                            />,
                        )}
                    </Form.Item>
                    <Form.Item label="Firstname" className="formitem">
                        {getFieldDecorator('firstname', {
                            initialValue:this.props.user.firstName,
                            rules: [{required: true, message: 'Please input your Firstname!'}],
                        })(
                            <Input
                                type="text"
                                placeholder="Firstname"

                            />,
                        )}
                    </Form.Item>
                    <Form.Item label="Lastname" className="formitem">
                        {getFieldDecorator('lastname', {
                            initialValue:this.props.user.lastName,
                            rules: [{required: true, message: 'Please input your lastname!'}],
                        })(
                            <Input
                                type="text"
                                placeholder="Lastname"

                            />,
                        )}
                    </Form.Item>
                    <Form.Item label="Email" className="formitem">
                        {getFieldDecorator('email', {
                            initialValue:this.props.user.email,
                            rules: [{required: true, message: 'Please input your email!'}],
                        })(
                            <Input
                                type="email"
                                placeholder="email"

                            />,
                        )}
                    </Form.Item>
                    <Form.Item label="Role" className="formitem" color="blue">
                          {getFieldDecorator('role', {
                            initialValue:this.props.user.roles,
                            rules: [{ required: true, message: 'Please select role!' }],
                          })(
                            <Select mode="multiple"
                              placeholder="Select a role"
                            >
                            {this.props.roles.map(role =>
                                 <option  key={role.id} value={role.name} >{role.name}</option>
                             )}

                            </Select>,
                              )}
                    </Form.Item>
                    <br/>
                    <div>
                        <Button type="primary" htmlType="submit" className="user-form-button"  loading={this.state.loading}>
                            Save
                        </Button>
                        <span>&nbsp;&nbsp;</span>
                        <Button type="secondary" className="user-form-button"  onClick={this.close}>
                            Close
                        </Button>
                    </div>

                    {validationError && <Text type="danger">{validationError}</Text>}
               </Form>
            </Card>


         )
    }
}