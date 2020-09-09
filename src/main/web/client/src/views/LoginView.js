import React, {Component} from 'react';
import UserService from "../services/UserService";
import {EventBus} from "../components/event";
import {Button, Col, Form, Icon, Input, Row, Typography, Card} from 'antd';
import {withRouter} from 'react-router-dom';
import {history} from "../helpers/history";
import {Router} from 'react-router';

const {Text} = Typography;


class LoginView extends Component {

    componentDidMount() {
        if (UserService.isLoggedIn()) {
            history.push("/#/");
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
        let self = this;
        UserService.login(username, password)
            .then(token => {
                const {from} = history.state || {from: {pathname: "/"}};
                history.push(from);
                /* EventBus.publish('login-logout', {});*/
                window.location.reload();

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
                <Col xl={{span: 8}} lg={{span: 6}} md={{span: 4}} sm={{span: 2}} xs={{span: 2}}/>
                <Col xl={{span: 8}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>
                    <Card title={"SYNVISION"} style={{top: 50}}>
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
                                <Button type="primary" htmlType="submit" block className="login-form-button">
                                    Log in
                                </Button>
                            </Form.Item>
                        </Form>
                        {loginError && <Text type="danger">{loginError}</Text>
                        }
                    </Card>
                </Col>
            </Row>

        )
    }

}

export default withRouter(LoginView);
