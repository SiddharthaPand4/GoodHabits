import React, {Component} from 'react';
import UserService from "../services/UserService";
import {EventBus} from "../components/event";
import {Button, Col, Form, Icon, Input, Row, Typography} from 'antd';
import { withRouter } from 'react-router-dom';
import {history} from "../helpers/history";

const {Text} = Typography;


class LoginView extends Component {

    componentDidMount() {
        if (UserService.isLoggedIn()) {
            history.push( "/");
        }
    }

    render() {
        const WrappedLoginForm = Form.create({name: 'login_form'})(LoginForm);
        return (
            <WrappedLoginForm/>
        )
    }
}

class LoginForm extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: '',
            submitted: false,
            loading: false,
            loginError: '',
        };

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(e) {

        e.preventDefault();

        const form = this.props.form;
        const username = form.getFieldValue("username");
        const password = form.getFieldValue("password");
        this.setState({submitted: true});

        if (!(username && password)) {
            return
        }

        this.setState({loading: true});
        UserService.login(username, password)
            .then(token => {
                history.push( "/");
                EventBus.publish('login-logout', {})
            }).catch(error => {
                this.setState({
                    loginError: "Something went wrong, please try again",
                    loading: false
                })
            });
    }

    render() {

        const {getFieldDecorator} = this.props.form;
        const loginError = this.state.loginError;

        return (
            <Row>
                <Col span={8} offset={8}><h4>Login</h4>
                    <Form onSubmit={this.handleSubmit} className="login-form">
                        <Form.Item>
                            {getFieldDecorator('username', {
                                rules: [{required: true, message: 'Please input your username!'}],
                            })(
                                <Input
                                    prefix={<Icon type="user" style={{color: 'rgba(0,0,0,.25)'}}/>}
                                    placeholder="Username"
                                />,
                            )}
                        </Form.Item>
                        <Form.Item>
                            {getFieldDecorator('password', {
                                rules: [{required: true, message: 'Please input your Password!'}],
                            })(
                                <Input
                                    prefix={<Icon type="lock" style={{color: 'rgba(0,0,0,.25)'}}/>}
                                    type="password"
                                    placeholder="Password"
                                />,
                            )}
                        </Form.Item>
                        <Form.Item>
                            <Button type="primary" htmlType="submit" className="login-form-button">
                                Log in
                            </Button>
                        </Form.Item>
                    </Form>
                    {loginError && <Text type="danger">{loginError}</Text>
                    }
                </Col>
            </Row>

        )
    }

}

export default withRouter(LoginView);
