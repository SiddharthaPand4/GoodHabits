import React, {Component} from 'react';
import {Card, Button, Col, Icon, message, Tag} from "antd";
import Row from "antd/es/grid/row";
import FeedService from "../../services/FeedService";


export default class ConfigView extends Component {
    constructor(props) {
        super(props);

        this.state = {
            isPlaying: true,
        }

        this.startFeed = this.startFeed.bind(this);
        this.stopFeed = this.stopFeed.bind(this);

    }

    startFeed(feedId) {

        FeedService.startFeed(feedId)
            .then(res => {
                message.success("Feed started")
                this.state.port = res.data
                console.log(res.data)
                //window.location.reload();

            }).catch(err => {
            message.error("Something Went Wrong!")
        })
    }

    stopFeed(feedId) {
        FeedService.stopFeed(feedId).then(res => {
            message.success("Feed stoped")

        }).catch(err => {
            message.error("Something Went Wrong!")
        })
    }


    render() {
        let isPlaying = this.state.isPlaying;
        return (
            <div>

                <Card>
                    <Row>

                        <Col span={12}>

                            <br/><br/>&nbsp;&nbsp;<Button
                            onClick={() => this.startFeed(this.props.location.feed.id)}><Icon type="play-circle"/>Start
                            Feed</Button>
                            &nbsp;&nbsp;<Button onClick={() => this.stopFeed(this.props.location.feed.id)}><Icon
                            type="pause-circle"/>Stop Feed</Button>
                            &nbsp;&nbsp;<Button
                            onClick={this.capture}><Icon type="camera"/>Capture</Button><br/><br/>
                            <Tag
                                color="#f50">{this.props.location.feed.site} / {this.props.location.feed.location} / {this.props.location.feed.name}/ {this.props.location.feed.url}</Tag>
                            <br/><br/>
                            {isPlaying
                                ? <img style={{border: "1px solid black"}} id="video" controls width="500" height="260"
                                       src={"http://localhost:" + this.state.port + "/stream"}/>


                                :
                                <img style={{border: "1px solid black"}} id="video" controls width="500" height="260"
                                     src={"http://localhost:" + this.state.port + "/ss"}/>
                            }
                            <br/><br/><Button style={{width: "500px"}} type="primary"
                                              onClick={() => this.setState({isPlaying: !isPlaying})}>PLAY/PAUSE</Button>
                        </Col>
                    </Row>
                </Card>

            </div>
        );
    }


}