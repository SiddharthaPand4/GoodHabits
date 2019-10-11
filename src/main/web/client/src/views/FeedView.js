import React, {Component} from "react";
import {Button, Card, Col, Form, Input, Row, Tag, Typography} from "antd";
import FeedService from "../services/FeedService";
import {EventBus} from "../components/event";
const {Text} = Typography;

export default class FeedView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            loading: true,
            videoVisible: false,
            feeds: {},
        };

        EventBus.subscribe('feed-refresh', (event) => this.refresh())
        this.removeFeed = this.removeFeed.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        FeedService.getFeeds().then(request => {
            this.setState({"feeds": request.data, loading: false})
        })
    }

    removeFeed(feed) {
        FeedService.removeFeed(feed).then(() => {
            this.refresh()
        })
    }

    startFeed(feed) {
        FeedService.startFeed(feed).then(() => {
            //this should force re-render :)
            this.setState({"refresh_state": Math.random()})
        })
    }

    stopFeed(feed) {
        FeedService.stopFeed(feed).then(() => {
            //this should force re-render :)
            this.setState({"refresh_state": Math.random()})
        })
    }

    render() {

        let feeds = this.state.feeds;

        if (this.state.loading || !this.state.feeds || this.state.feeds.length === 0) {
            feeds = []
        }

        const WrappedFeedForm = Form.create({name: 'feed_form'})(FeedForm);

        return (
            <Row gutter={16}>
                {
                    feeds.map((feed, index) =>

                        <Col span={8} key={index}>

                            <Card
                                title={
                                    <Tag color="#f50">{feed.site}/{feed.location}/{feed.name}</Tag>
                                }
                                cover={<img alt="feedview" src={"/api/feed/" + feed.ID+ "/view"}/>}
                                bordered={true}
                                actions={[
                                    <Button onClick={() => {this.startFeed(feed)}}>Start</Button>,
                                    <Button onClick={() => {this.stopFeed(feed)}}>Stop</Button>,
                                    <Button onClick={() => {this.removeFeed(feed)}}>Delete</Button>
                                ]}
                            >

                            </Card>
                        </Col>
                    )
                }

                <Col span={8} key={-1}>

                    <Card bordered={true}>
                        <WrappedFeedForm/>
                    </Card>
                </Col>
            </Row>
        )
    }
}

function hasErrors(fieldsError) {
    return Object.keys(fieldsError).some(field => fieldsError[field]);
}

class FeedForm extends Component {
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
        var feed = {};
        feed.url = form.getFieldValue("url");
        feed.site = form.getFieldValue("site");
        feed.location = form.getFieldValue("location");
        feed.name = form.getFieldValue("name");
        let validationError;
        if (!feed.url) {
            validationError = "Missing url"
        }

        if (!feed.site) {
            validationError = "Missing site"
        }

        if (!feed.location) {
            validationError = "Missing location"
        }

        if (!feed.name) {
            validationError = "Missing name"
        }

        if (validationError) {
            this.setState({validationError: validationError});
            return
        }

        console.log('saving feed', feed);
        this.setState({submitted: true, loading: true});

        FeedService.addFeed(feed).then(response => {
            EventBus.publish('feed-refresh', {})
        });
    }

    render() {

        const {getFieldDecorator, getFieldsError} = this.props.form;
        const validationError = this.state.validationError;
        return (
            <Form onSubmit={this.handleSubmit}>
                <Form.Item>
                    {getFieldDecorator('url', {rules: [{required: true, message: 'enter feed url!'}],})(
                        <Input addonBefore="Feed&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" placeholder="rtsp://"/>,
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('site', {rules: [{required: true, message: 'enter site!'}],})(
                        <Input addonBefore="Site&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                               placeholder="Site (e.g. gurgaon)"/>,
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('location', {rules: [{required: true, message: 'enter location!'}],})(
                        <Input addonBefore="Location" placeholder="Location (e.g. 9th floor)"/>,
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('name', {rules: [{required: true, message: 'enter name!'}],})(
                        <Input addonBefore="Name&nbsp;&nbsp;&nbsp;&nbsp;" placeholder="Name (e.g. front gate )"/>,
                    )}
                </Form.Item>
                <Button htmlType="submit" type="primary" disabled={hasErrors(getFieldsError())}>Add</Button>
                {validationError && <Text type="danger">{validationError}</Text>}
            </Form>
        )
    }
}