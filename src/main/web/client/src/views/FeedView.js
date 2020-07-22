import React, {Component} from "react";
import {Button, Card, Col, Divider, Form, Input, message, Modal, Row, Spin, Table, Tag, Typography,Icon} from "antd";
import FeedService from "../services/FeedService";
import {EventBus} from "../components/event";
import ButtonGroup from "antd/es/button/button-group";
import {Link} from "react-router-dom";



const {Text} = Typography;
const {Column} = Table;
const { confirm } = Modal;
let flag=false;// true to enable editing & false for adding new feed
export default class FeedView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            loading: true,
            videoVisible: false,
            formVisible:false,
            layout: "list", //list || grid
            mode:"Add", //Add || Edit
            feed:{url:"",site:"",location:"",name:"",id:""},
            feeds: []
        };

        EventBus.subscribe('feed-refresh', (event) => this.refresh())
        this.addFeeds=this.addFeeds.bind(this);
        this.close=this.close.bind(this);
        this.showFeed=this.showFeed.bind(this);
        this.showDeleteConfirm=this.showDeleteConfirm.bind(this);
        this.refresh=this.refresh.bind(this);
    }

    componentDidMount() {
        this.refresh();
    }

    refresh() {
        FeedService.getFeeds().then(request => {
            this.setState({"feeds": request.data, loading: false})
        })
    }
    refreshFeed(){
        this.refresh();
    }

    showDeleteConfirm(feedId,refresh) {
        confirm({
            title: 'Are you sure you want to Delete this Feed',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk() {
                FeedService.removeFeed(feedId)
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

 // startFeed(feed) {
 //     FeedService.startFeed(feed).then(() => {
 //         //this should force re-render :)
 //         this.setState({"refresh_state": Math.random()})
 //     })
 // }

 // stopFeed(feed) {
 //     FeedService.stopFeed(feed).then(() => {
 //         //this should force re-render :)
 //         this.setState({"refresh_state": Math.random()})
 //     })
 // }


    changeLayout(layout) {
        this.setState({"layout": layout});
    }

    addFeeds(){
    this.setState({formVisible:true,mode:"Add",feed:{url:"",site:"",location:"",name:""},})
    flag=false;
    }
    close(){
        this.setState({"formVisible":false})
    }


    showFeed(FeedId){
        this.setState({"formVisible":true,"mode":"Edit"})
        flag=true;
        FeedService.getFeed(FeedId)
            .then(response =>{
                this.setState({feed : response.data})
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
                    <br/>
                    <span>&nbsp;&nbsp;</span>
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
                        <Col span={15}>
                                <Card>
                                    <Table dataSource={this.state.feeds} pagination={false}>
                                        <Column title="Feed URL" dataIndex="url" key="url"/>
                                        <Column title="Location" dataIndex="location" key="location"/>
                                        <Column title="Name" dataIndex="name" key="name"/>
                                        <Column title="Site" dataIndex="site" key="site"/>
                                        <Column title="Action" render={(text, record) => (
                                            <span>
                                             <Icon type="edit" onClick={()=>this.showFeed(record.id)}/>
                                           <Divider type="vertical" />
                                           <Icon type="delete" style={{color: "#ff0000"}} onClick={()=>this.showDeleteConfirm(record.id,this.refresh)}/>
                                           <Divider type="vertical" />
                                           <Link to={ {pathname:'/feedStream',
                                               feed:record
                                               }
                                           }>
                                           <Icon type="play-circle" /></Link>
                                            </span>
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
                                            <Link to={{pathname:'/feedStream',
                                                feed:feed
                                            }}>
                                                <Button>Play</Button></Link>,
                                            <Button onClick={() => {
                                                this.showDeleteConfirm(feed.id,this.refresh)
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
                    <Col span={9}>
                        {this.state.mode == "Add" ?
                            <Card title="Add Feed">
                                <WrappedFeedForm feed={this.state.feed} close={this.close}/>
                            </Card>
                            :
                            <Card title="Edit Feed">
                                <WrappedFeedForm feed={this.state.feed} refresh={this.refreshFeed} close={this.close}/>

                            </Card>
                        }
                    </Col>
                    : null
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
            feed:{url:"",site:"",location:"",name:""}
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.close = this.close.bind(this);
    }
close()
{
    this.props.close();
}


    handleSubmit(e) {

        e.preventDefault();

        const form = this.props.form;
        var feed = {};
        feed.url = form.getFieldValue("url");
        feed.site = form.getFieldValue("site");
        feed.location = form.getFieldValue("location");
        feed.name = form.getFieldValue("name");
        feed.id=this.props.feed.id
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

        FeedService.addFeed(feed,flag).then(response => {
            if(flag){
                message.success("Feed updated")
                this.close()
            }
            else{
                message.success("Feed Added")
            }
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
                                {getFieldDecorator('url', {
                                        initialValue:this.props.feed.url,
                                        rules: [{required: true, message: 'enter feed url!'}],})(
                                    <Input addonBefore="Feed&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                                           placeholder="rtsp://"/>,
                                )}
                            </Form.Item>
                            <Form.Item>
                                {getFieldDecorator('site', {
                                    initialValue:this.props.feed.site,
                                    rules: [{required: true, message: 'enter site!'}],})(
                                    <Input addonBefore="Site&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                                           placeholder="Site (e.g. gurgaon)"/>,
                                )}
                            </Form.Item>
                            <Form.Item>
                                {getFieldDecorator('location', {
                                    initialValue:this.props.feed.location,
                                    rules: [{
                                        required: true,
                                        message: 'enter location!'
                                    }],
                                })(
                                    <Input addonBefore="Location" placeholder="Location (e.g. 9th floor)"/>,
                                )}
                            </Form.Item>
                            <Form.Item>
                                {getFieldDecorator('name', {
                                    initialValue:this.props.feed.name,
                                    rules: [{required: true, message: 'enter name!'}],})(
                                    <Input addonBefore="Name&nbsp;&nbsp;&nbsp;&nbsp;"
                                           placeholder="Name (e.g. front gate )"/>,
                                )}
                            </Form.Item>
                            <Button htmlType="submit" type="primary" disabled={hasErrors(getFieldsError())}>Save</Button>
                            {validationError && <Text type="danger">{validationError}</Text>}
                      <span>&nbsp;&nbsp;</span>
                      <Button type="secondary" className="user-form-button" size="small" onClick={this.close}>
                          Close
                      </Button>

                        </Form>
            </div>
        )
    }
}