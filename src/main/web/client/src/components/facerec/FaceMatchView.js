import React, {Component} from "react";
import Webcam from "react-webcam";
import {Button, Col, Form, Input, Row} from "antd";
import FaceMatchService from "../../services/facerec/FaceMatchService";
import FeedService from "../../services/FeedService";
import {EventBus} from "../event";

export default class FaceMatchView extends Component  {

    constructor(props) {
        super(props);

        this.state = {
            captured:false,
            userdata:{},
            image:"blank"
        };
        this.webcamRef = React.createRef();
        this.capture = this.capture.bind(this);
        this.lookup = this.lookup.bind(this);
        this.register = this.register.bind(this);
        this.resetcamera = this.resetcamera.bind(this);
    }

    capture() {
        var image = this.webcamRef.current.getScreenshot();
        this.setState({image:image, captured: true});
        console.log("gotta", image)
    }

    resetcamera() {
        this.setState({image:"blank", captured: false});
    }

    lookup() {
        let data = FaceMatchService.lookup(this.state.image);
        this.setState({userdata : data})
    }

    register() {
        //FaceMatchService.register(this.state.image, formdata)
    }

    render() {

        const videoConstraints = {
            width: 400,
            height: 400,
            facingMode: "environment"
        };

        const WrapperUserForm = Form.create({name: 'user_form'})(UserForm);
        let elmnt;
        if (this.state.captured) {
            elmnt = <img src={this.state.image}/>
        }
        else {
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
                    <Button onClick={this.capture}>Capture photo</Button>
                    <Button onClick={this.resetcamera}>Reset</Button>
                </Col>
                <Col md={6}>
                    <WrapperUserForm userdata={this.state.userdata}/>
                    <Button onClick={this.lookup}>Lookup</Button>
                    <Button onClick={this.register}>Register</Button>
                </Col>
            </Row>
        </div>)
    }
}

class UserForm extends Component {
    constructor(props) {
        super(props);

        this.state = {
            id: '',
            name: '',
            address:'',
            submitted: false,
            loading: false,
            loginError: '',
            userdata:this.props.userdata
        };

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount() {
        this.props.form.setFieldsValue({
            id : this.state.userdata.id,
            name: this.state.userdata.name,
            address: this.state.userdata.address
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

        if (validationError) {
            this.setState({validationError: validationError});
            return
        }

        console.log('registering user', userdata);
        this.setState({submitted: true, loading: true});

        FaceMatchService.register(userdata)
    }

    render() {
        const {getFieldDecorator, getFieldsError} = this.props.form;
        const validationError = this.state.validationError;

        return (
            <Form onSubmit={this.handleSubmit}>
                <Form.Item>
                    {getFieldDecorator('id', {rules: [{required: true, message: 'enter id!'}],})(
                        <Input addonBefore="ID" placeholder="ID"/>,
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('name', {rules: [{required: true, message: 'enter name!'}],})(
                        <Input addonBefore="name" placeholder="name"/>,
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('address', {rules: [{required: true, message: 'enter address!'}],})(
                        <Input addonBefore="address" placeholder="address"/>,
                    )}
                </Form.Item>
            </Form>
        )
    }
}
