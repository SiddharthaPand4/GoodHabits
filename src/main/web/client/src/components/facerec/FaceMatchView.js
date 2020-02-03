import React, {Component} from "react";
import Webcam from "react-webcam";
import {Button, ButtonGroup, Col, Form, Input, Row, Typography} from "antd";
import FaceMatchService from "../../services/facerec/FaceMatchService";
import {EventBus} from "../event";

const {Text} = Typography;

export default class FaceMatchView extends Component {

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
                <Col md={6}>
                    {elmnt}
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
            address: '',
            submitted: false,
            loading: false,
            loginError: '',
            userdata: this.props.userdata
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.refresh = this.refresh.bind(this);
        this.screenshot = this.screenshot.bind(this);
        this.lookup = this.lookup.bind(this);

        EventBus.subscribe('frs-refresh', (data) => this.refresh(data));
        EventBus.subscribe('frs-screenshot', (data) => this.screenshot(data));

    }

    componentDidMount() {

        console.log('component mounted');

        this.props.form.setFieldsInitialValue({
            id: this.state.userdata.id,
            name: this.state.userdata.name,
            address: this.state.userdata.address
        })
    }

    componentWillUnmount() {
        console.log('component unmounted');
    }

    screenshot(image) {
        console.log('rcvd image on bus', image);
        this.setState({image: image});
    }

    lookup() {
        FaceMatchService.lookup(this.state.image).then(response => {
            this.setState({userdata: response.data});
            EventBus.publish('frs-refresh', response.data)
        }).catch(function (error) {
            if (error.response.data.message) {
                self.setState({validationError: error.response.data.message});
            }
        });
    }

    refresh(userdata) {
        console.log("got refresh with userdata:", userdata);
        this.props.form.setFieldsValue({
            id: userdata.id,
            name: userdata.name,
            address: userdata.address
        })
    }


    handleSubmit(e) {
        e.preventDefault();

        const form = this.props.form;
        var userdata = {};
        userdata.id = form.getFieldValue("id");
        userdata.name = form.getFieldValue("name");
        userdata.address = form.getFieldValue("address");

        let validationError;
        if (!userdata.id) {
            validationError = "Missing ID"
        }

        if (!userdata.name) {
            validationError = "Missing name"
        }

        if (!userdata.address) {
            validationError = "Missing address"
        }

        if (!this.state.image) {
            validationError = "First Capture image"
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
            console.log(response)
        }).catch(function (error) {
            if (error.response.data.message) {
                self.setState({validationError: error.response.data.message});
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
                <Form.Item>
                    {getFieldDecorator('address', {rules: [{required: true, message: 'enter address!'}],})(
                        <Input addonBefore="Address"/>,
                    )}
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