import React, {Component} from "react";
import {Button, Card, Col, Divider, Form, Input, message, Row, Spin, Table, Tag, Typography} from "antd";
import FeedService from "../services/FeedService";
import {EventBus} from "../components/event";
import ButtonGroup from "antd/es/button/button-group";


const {Text} = Typography;
const {Column} = Table;
export default class FeedView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            loading: true,
            videoVisible: false,
            formVisible:false,
            layout: "list", //list || grid
            feeds: [],
        };

        //EventBus.subscribe('feed-refresh', (event) => this.refresh())
        this.removeFeed = this.removeFeed.bind(this);
        this.addFeeds=this.addFeeds.bind(this);
        this.close=this.close.bind(this);
        this.showFeed=this.showFeed.bind(this);
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
        FeedService.removeFeed(feed.url)
            .then(() => {
            this.refresh()
                message.success("Deleted Successfully!")
        }).catch(error => {
            let msg = "Something went wrong!";
            if (error && error.response && error.response.data && error.response.data.message) {
                msg = error.response.data.message;
                message.warn(msg);
            }

        });
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
    changeLayout(layout) {
        this.setState({"layout": layout});
    }

    addFeeds(){
    this.setState({"formVisible":true})
    }
    close(){
        this.setState({"formVisible":false})
    }


    showFeed(url){
        this.setState({"formVisible":true})
        FeedService.getFeed(url)
            .then(response =>{console.log(response.data)
            },
            error=>{
                message.error(error.response.data.message);
            });
    }

    render() {
        let layout = this.state.layout;
        let feeds = this.state.feeds;

        if (this.state.loading || !this.state.feeds || this.state.feeds.length === 0) {
            feeds = []
        }

        const WrappedFeedForm = Form.create({name: 'feed_form'})(FeedForm);

        return (
            <div>
                <Row gutter={10}>
                    <Button type="primary" onClick={this.addFeeds}>
                        + New Feed
                    </Button>&nbsp;&nbsp;&nbsp;
                    <ButtonGroup >
                        <Button type={layout === "list" ? "primary" :""} icon="unordered-list"
                                onClick={() => {
                                    this.changeLayout("list")
                                }}>List </Button>
                        <Button type={layout === "grid" ? "primary" : ""} icon="table"
                                onClick={() => {
                                    this.changeLayout("grid")
                                }}>Grid</Button>
                    </ButtonGroup>
                    <br/>
                    <br/>
                    {layout === "list"
                        ?
                        (this.state.feeds && this.state.feeds.length > 0)
                            ?
                            <Col span={12}>
                                <Card>
                                    <Table dataSource={this.state.feeds} pagination={false}>
                                        <Column title="Feed URL" dataIndex="url" key="url"/>
                                        <Column title="Location" dataIndex="location" key="location"/>
                                        <Column title="Name" dataIndex="name" key="name"/>
                                            <Column title="Site" dataIndex="site" key="site"/>
                                            <Column title="Action" render={(text, record) => (
                                            <a onClick={this.showFeed.bind(this,record.url)}>Edit</a>
                                            )}/>
                                    </Table>

                                </Card>
                            </Col>
                            : null
                        :
                    <Row gutter={16}>
                        {
                            feeds.map((feed, index) =>

                                <Col span={8} key={index}>

                                    <Card
                                        title={
                                            <Tag color="#f50">{feed.site}/{feed.location}/{feed.name}</Tag>
                                        }
                                        cover={<img alt="feedview" src={"/api/feed/" + feed.ID + "/view"}/>}
                                        bordered={true}
                                        actions={[
                                            <Button onClick={() => {
                                                this.startFeed(feed)
                                            }}>Start</Button>,
                                            <Button onClick={() => {
                                                this.stopFeed(feed)
                                            }}>Stop</Button>,
                                            <Button onClick={() => {
                                                this.removeFeed(feed)
                                            }}>Delete</Button>
                                        ]}
                                    >

                                    </Card>
                                    <br/>
                                </Col>
                            )
                        }
                        </Row>}


                    {this.state.formVisible ?
                    <Col span={12}>
                        <Card title="Add Feed" >
                            <WrappedFeedForm/>
                            <Button type="secondary" className="user-form-button"  size="small" onClick={this.close}>
                                Close
                            </Button>
                        </Card>
                    </Col>
                    :null
                }
                </Row>




            </div>


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
            message.success("Feed Saved Successfully")
            EventBus.publish('feed-refresh', {})
        }).catch(error => {
            let msg = "Something went wrong!";
            if (error && error.response && error.response.data && error.response.data.message) {
                msg = error.response.data.message;
                message.warn(msg);
            }

        });
    }

    render() {

        const {getFieldDecorator, getFieldsError} = this.props.form;
        const validationError = this.state.validationError;
        return (
            <div>

                  <Form title={"ADD FEED"} onSubmit={this.handleSubmit}>
                            <Form.Item>
                                {getFieldDecorator('url', {rules: [{required: true, message: 'enter feed url!'}],})(
                                    <Input addonBefore="Feed&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                                           placeholder="rtsp://"/>,
                                )}
                            </Form.Item>
                            <Form.Item>
                                {getFieldDecorator('site', {rules: [{required: true, message: 'enter site!'}],})(
                                    <Input addonBefore="Site&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                                           placeholder="Site (e.g. gurgaon)"/>,
                                )}
                            </Form.Item>
                            <Form.Item>
                                {getFieldDecorator('location', {
                                    rules: [{
                                        required: true,
                                        message: 'enter location!'
                                    }],
                                })(
                                    <Input addonBefore="Location" placeholder="Location (e.g. 9th floor)"/>,
                                )}
                            </Form.Item>
                            <Form.Item>
                                {getFieldDecorator('name', {rules: [{required: true, message: 'enter name!'}],})(
                                    <Input addonBefore="Name&nbsp;&nbsp;&nbsp;&nbsp;"
                                           placeholder="Name (e.g. front gate )"/>,
                                )}
                            </Form.Item>
                            <Button htmlType="submit" type="primary" disabled={hasErrors(getFieldsError())}>Add</Button>
                            {validationError && <Text type="danger">{validationError}</Text>}


                        </Form>
            </div>
        )
    }
}