import React, {Component} from "react";
import {Table, Divider,Row,Col,Card,Form,Button,Input,Icon,Typography,Select,Modal,message} from 'antd';
import UserService from "../services/UserService";
import {EventBus} from "../components/event";
const { Column} = Table;
const {Text} = Typography;
const { confirm } = Modal;
const { Option } = Select;
const ButtonGroup = Button.Group;
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
        }

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

    showModal = () => {
        this.setState({
          visible: true,
        });
    };


    addUser(){
        this.setState({mode:"Add",showUserDetails:true, user:{userName:"", lastName:"",firstName:"",email:"",password:""}})
    }

    showUser(userId){
        this.setState({showUserDetails:true,mode:"Edit"})
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
     const mode=this.state.mode;
     const user=this.state.user;

        return (
         <div>
            <Row gutter={16}>
                <Col span={2}>
                    <Button type="primary" onClick={this.addUser}>
                         + ADD
                    </Button>
                </Col>
            </Row>
            <br/>
            <Row gutter={16}>
                <Col span={12}>
                    <Table dataSource={this.state.users} >
                        <Column title="Username" dataIndex="userName" key="userName" render={(text, record) => (
                            <span><a href={"user/" + record.ID}>{text}</a></span>
                        )}/>
                        <Column title="Email" dataIndex="email" key="email" />
                        <Column title="Token" dataIndex="token" key="token" />
                        <Column title="Action" key="action" render={(text, record) => (
                                <span>
                                    <a onClick={this.showUser.bind(this,record.id)}>Edit</a>
                                    <Divider type="vertical" />
                                    <a onClick={this.showDeleteConfirm.bind(this,record.id,this.refresh)}>Delete</a>
                                </span>
                            )}
                        />
                    </Table>

                </Col>

               {showUserDetails && mode=="Edit"  ?  <Col span={12}>
                    <Card title="Edit User">

                        <WrappedUserForm user={this.state.user} roles={this.state.roles} refresh={this.refreshUsers} close={this.close}/>
                    </Card>
                </Col>
                :null}

               {showUserDetails && mode=="Add"  ?  <Col span={12}>
                    <Card title="Add User">
                        <WrappedUserForm user={this.state.user} roles={this.state.roles} refresh={this.refreshUsers}  close={this.close}/>
                    </Card>
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
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.close = this.close.bind(this);

    }



 //componentWillReceiveProps(nextProps){
 //  if (this.state.user !== nextProps.user) {
 //     this.setState({user:nextProps.user})
 //     this.props.form.setFieldsValue({
 //           username: nextProps.user.userName,
 //           firstname: nextProps.user.firstName,
 //           lastname: nextProps.user.lastName,
 //           email:this.state.user.email
 //    })
 //  }
 //}

    close(){
        this.props.close();
    }

    handleSubmit(e) {

        e.preventDefault();

        const form = this.props.form;
        var user = {};
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
        if(!user.password && this.props.mode=="Add"){
            validationError="Missing password"
        }
        if(user.roles.length==0){
            validationError="Missing role"
        }
        if (validationError) {
            this.setState({validationError: validationError});
            return
        }
        console.log('saving user', user);
        this.setState({submitted: true, loading: true});
        UserService.createUser(user).then(response => {
            if(user.id){
                 message.success("User updated")
            }
            else{
                 message.success("User created")
            }
            this.props.refresh();

        }).catch(error=> {
            console.log(error);
        });
    }



    render() {

        const {getFieldDecorator} = this.props.form;
        const validationError = this.state.validationError;

         return (
            <div>
             <Form onSubmit={this.handleSubmit} className="user-form">
                        <Form.Item>
                            {getFieldDecorator('username', {
                                initialValue:this.props.user.userName,
                                rules: [{required: true, message: 'Please input your username!'}],
                            })(
                                <Input
                                    type="text"
                                    addonBefore="Username"
                                    placeholder="Username"


                                />,
                            )}
                        </Form.Item>
                        <Form.Item>
                            {getFieldDecorator('firstname', {
                                initialValue:this.props.user.firstName,
                                rules: [{required: true, message: 'Please input your Firstname!'}],
                            })(
                                <Input
                                    addonBefore="Firstname"
                                    type="text"
                                    placeholder="Firstname"

                                />,
                            )}
                        </Form.Item>
                        <Form.Item>
                            {getFieldDecorator('lastname', {
                                initialValue:this.props.user.lastName,
                                rules: [{required: true, message: 'Please input your lastname!'}],
                            })(
                                <Input
                                    type="text"
                                    addonBefore="Lastname"
                                    placeholder="Lastname"

                                />,
                            )}
                        </Form.Item>
                        <Form.Item>
                            {getFieldDecorator('email', {
                                initialValue:this.props.user.email,
                                rules: [{required: true, message: 'Please input your email!'}],
                            })(
                                <Input
                                    type="text"
                                    addonBefore="Email"
                                    placeholder="email"

                                />,
                            )}
                        </Form.Item>
                        <Form.Item label="Role">
                                  {getFieldDecorator('role', {
                                    initialValue:this.props.user.roles,
                                    rules: [{ required: true, message: 'Please select role!' }],
                                  })(
                                    <Select mode="multiple"
                                      placeholder="Select a role"
                                    >
                                    {this.props.roles.map(role =>
                                         <option key={role.id} value={role.name} >{role.name}</option>
                                     )}

                                    </Select>,
                                  )}
                        </Form.Item>

                        <div align="right">
                            <Button type="primary" htmlType="submit" className="user-form-button">
                                Save
                            </Button>
                            <span>&nbsp;&nbsp;</span>
                            <Button type="secondary"  onClick={this.close}>
                                Close
                            </Button>
                        </div>



                        {validationError && <Text type="danger">{validationError}</Text>}
                    </Form>
              </div>

        )
    }
}