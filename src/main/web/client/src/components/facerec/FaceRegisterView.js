import React, {Component} from "react";
import Webcam from "react-webcam";
import {Button, message, Col, Form, Input, Row, Select, Typography} from "antd";
import FaceMatchService from "../../services/facerec/FrsService";
import {EventBus} from "../event";

const {Text} = Typography;

export default class FaceRegisterView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            captured: false,
            userdata: {},
            image: "blank"
        };
        this.webcamRef = React.createRef();
        this.capture = this.capture.bind(this);
        this.resetcamera = this.resetcamera.bind(this);
    }

    componentDidMount() {
        setInterval(() => {
            this.setState({
                curTime : new Date().toLocaleString('in-IN')
            })
        }, 1000)
    }

    capture() {
        var image = this.webcamRef.current.getScreenshot();
        console.log('publishing image on bus', image);
        EventBus.publish('frs-screenshot', image);
        this.setState({image: image, captured: true});
    }

    resetcamera() {
        this.setState({image: "blank", captured: false, userdata: {}});
        EventBus.publish('frs-refresh', {})
    }

    render() {

        const videoConstraints = {
            width: 400,
            height: 400,
            facingMode: "environment"
        };

        let elmnt;
        if (this.state.captured) {
            elmnt = <img src={this.state.image}/>
        } else {
            elmnt = <Webcam
                width={400}
                height={400}
                audio={false}
                screenshotFormat="image/jpeg"
                videoConstraints={videoConstraints}
                ref={this.webcamRef}/>
        }
        return (<div>
            <Row>
                <Col md={12} >
                    {elmnt}
                    <br/>
                    <div><h5>Registration Time: {this.state.curTime}</h5></div>
                    <br/>
                    <Button type="primary" onClick={this.capture}>Capture photo</Button>{' '}
                    <Button onClick={this.resetcamera}>Reset</Button>
                </Col>
                <Col md={6}>

                    <WrapperUserForm userdata={this.state.userdata}/>
                </Col>
            </Row>
        </div>)
    }
}

function hasErrors(fieldsError) {
    return Object.keys(fieldsError).some(field => fieldsError[field]);
}

class UserForm extends Component {
    constructor(props) {
        super(props);

        this.state = {
            id: '',
            name: '',
            submitted: false,
            loading: false,
            loginError: '',
            userdata: this.props.userdata
        };

        this.state.userdata.type = "Employee";
        this.state.userdata.accessType = "WhiteList";
        this.handleSubmit = this.handleSubmit.bind(this);
        this.refresh = this.refresh.bind(this);
        this.screenshot = this.screenshot.bind(this);
        this.lookup = this.lookup.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleAccessTypeChange = this.handleAccessTypeChange.bind(this);
        EventBus.subscribe('frs-refresh', (data) => this.refresh(data));
        EventBus.subscribe('frs-screenshot', (data) => this.screenshot(data));

    }

    componentDidMount() {

        console.log('component mounted');

        this.props.form.setFieldsInitialValue({
            id: this.state.userdata.id,
            name: this.state.userdata.name,
            type: this.state.userdata.type,
            accessType: this.state.userdata.accessType
        })


    }

    componentWillUnmount() {
        console.log('component unmounted');
    }

    handleChange(value) {
        let userdata = this.state.userdata;
        userdata.type = value;
        this.setState({userdata: userdata});
        console.log(`selected ${value}`);
    }

    handleAccessTypeChange(value) {
        let userdata = this.state.userdata;
        userdata.accessType = value;
        this.setState({userdata: userdata});
        console.log(`selected ${value}`);
    }

    screenshot(image) {
        console.log('rcvd image on bus', image);
        this.setState({image: image});
    }

    lookup() {
        let self = this
        FaceMatchService.lookup(this.state.image).then(response => {
            this.setState({userdata: response.data});
            EventBus.publish('frs-refresh', response.data)
        }).catch(function (error) {
            if (error.response?.data?.message) {
                self.setState({validationError: error.response.data.message});
            }
            else {
                self.setState({validationError: "Server Error"});
            }
        });
    }

    refresh(userdata) {
        console.log("got refresh with userdata:", userdata);
        this.props.form.setFieldsValue({
            id: userdata.id,
            name: userdata.name,
            address: userdata.address,
            type: userdata.type
        })
    }


    handleSubmit(e) {
        e.preventDefault();

        const form = this.props.form;
        var userdata = {};
        userdata.id = form.getFieldValue("id");
        userdata.name = form.getFieldValue("name");
        userdata.type = this.state.userdata.type;
        userdata.accessType = this.state.userdata.accessType;
        let validationError;
        if (!userdata.id) {
            validationError = "Missing ID"
        }

        if (!userdata.name) {
            validationError = "Missing name"
        }

        if (!this.state.image) {
            validationError = "Capture image First by Click on Capture"
        }

        if (validationError) {
            this.setState({validationError: validationError});
            console.log("Error validating data", validationError);
            return
        }

        console.log('registering user', userdata);
        this.setState({submitted: true, loading: true});
        const self = this;
        FaceMatchService.register(userdata, this.state.image).then(function (response) {
            message.success("Registration sucessfull");
        }).catch(function (error) {
            message.error("Error registering user!");
            if (error.response?.data?.message) {
                self.setState({validationError: error.response.data.message});
            }
            else {
                self.setState({validationError: "Server Error"});
            }
        })
    }

    render() {
        const {getFieldDecorator, getFieldsError} = this.props.form;
        const validationError = this.state.validationError;

        return (
            <Form onSubmit={this.handleSubmit}>
                <Form.Item>
                    {getFieldDecorator('id', {rules: [{required: true, message: 'enter id!'}],})(
                        <Input addonBefore="ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"/>,
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('name', {rules: [{required: true, message: 'enter name!'}],})(
                        <Input addonBefore="Name&nbsp;&nbsp;&nbsp;"/>,
                    )}
                </Form.Item>
                <Form.Item label="Type">
                    <Select defaultValue="Employee" onChange={this.handleChange}>
                        <Select.Option value="Employee">Employee</Select.Option>
                        <Select.Option value="Vendor">Vendor</Select.Option>
                        <Select.Option value="Visitor">Visitor</Select.Option>
                        <Select.Option value="Others">Others</Select.Option>
                    </Select>
                </Form.Item>
                <Form.Item label="Access">
                    <Select defaultValue="WhiteList" onChange={this.handleAccessTypeChange}>
                        <Select.Option value="BlackList">BlackList</Select.Option>
                        <Select.Option value="WhiteList">WhiteList</Select.Option>
                    </Select>
                </Form.Item>
                <div>
                    <Button onClick={this.lookup}>Lookup</Button>{' '}
                    <Button htmlType="submit" type="primary" disabled={hasErrors(getFieldsError())}>Register</Button>
                </div>
                {validationError && <Text type="danger">{validationError}</Text>}
            </Form>
        )
    }
}

const WrapperUserForm = Form.create({name: 'user_form'})(UserForm);