import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Button, Col, Icon, message, Tag} from "antd";
import AnnotationService from "../../services/ConfigService";
import Row from "antd/es/grid/row";
import Card from "@material-ui/core/Card";
import FeedService from "../../services/FeedService";



export default class ConfigView extends Component {
    constructor(props) {
        super(props);

        this.state = {
            arrow: false,
            isDown: false,
            canvasWidth: 500,
            canvasHeight: 278,
            previousPointX: '',
            previousPointY: '',
            finalPointX: '',
            finalPointY: '',
            boxes: [],
            lines: [],
            isPlaying: true,
            image: null,
            dataURL: ""
        }
        this.handleMouseDown = this.handleMouseDown.bind(this);
        this.handleMouseMove = this.handleMouseMove.bind(this);
        this.handleMouseUp = this.handleMouseUp.bind(this);
        this.handleArrow = this.handleArrow.bind(this);
        this.drawArrow = this.drawArrow.bind(this);
        this.drawLine = this.drawLine.bind(this);
        this.capture = this.capture.bind(this);
        this.refresh=this.refresh.bind(this);

    }

//To store image use this.

    /*save = ()=>{
       AnnotationService.saveAnnotation(this.state.lines,this.state.dataURL)
           .then( res => {
               message.success("Annotation Saved")
               // this.setState({lines:null})
           }).catch( err => {

           message.error("Something went wrong")
       })
   }*/

    save = () => {

        AnnotationService.saveAnnotation(this.state.lines)
            .then(res => {
                message.success("Annotation Saved")

                // this.setState({lines:null})
            }).catch(err => {

            message.error("Something went wrong")
        })


    }
    refresh(){

    }

    startFeed(feedId){

        FeedService.startFeed(feedId)
            .then(res => {
            message.success("Feed started")
                //window.location.reload();

        }).catch(err => {
            message.error("Something Went Wrong!")
        })
    }

    stopFeed() {
        FeedService.stopFeed().then(res => {
            message.success("Feed stoped")

        }).catch(err => {
            message.error("Something Went Wrong!")
        })
    }

    componentDidMount() {
        this.drawLines();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        this.drawLines();
    }

    handleMouseDown(event) {
        console.log(event);
        this.setState({
            isDown: true,
            previousPointX: event.offsetX,
            previousPointY: event.offsetY
        }, () => {
            const canvas = this.refs.canvas;
            var x = event.offsetX;
            var y = event.offsetY;
            var ctx = canvas.getContext("2d");

            console.log(x, y);

        })
    }

    handleMouseMove(event) {
        let isDown = this.state.isDown;
        if (isDown === true) {

        }
    }

    handleMouseUp(event) {

        var x = event.offsetX;
        var y = event.offsetY;

        const canvas = this.refs.canvas;
        var ctx = canvas.getContext("2d");

        //for simple line
        let prevX = this.state.previousPointX;
        let prevY = this.state.previousPointY;
        let len = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow((y - prevY), 2));
        if (len < 5) {

            return;
        }

        let line = {
            x1: prevX / this.state.canvasWidth,
            y1: prevY / this.state.canvasHeight,
            x2: x / this.state.canvasWidth,
            y2: y / this.state.canvasHeight
        }
        let lines = this.state.lines;
        lines.push(line);
        this.setState({
            isDown: false,
            lines: lines
        });
    }

    handleArrow(event) {

        let lines = this.state.lines;

        const canvas = this.refs.canvas;
        var x = event.offsetX;
        var y = event.offsetY;
        var ctx = canvas.getContext("2d");

        var headlen = 20;
        var x2 = this.state.previousPointX;
        var y2 = this.state.previousPointY;

        let len = Math.sqrt(Math.pow(x - x2, 2) + Math.pow((y - y2), 2));
        if (len < 25) {

            ctx.beginPath();
            return;
        }

        lines.push({
            x1: x2 / this.state.canvasWidth,
            y1: y2 / this.state.canvasHeight,
            x2: x / this.state.canvasWidth,
            y2: y / this.state.canvasHeight
        });

        var dx = x - x2;
        var dy = y - y2;
        var angle = Math.atan2(dy, dx);


        let arrowX1 = x - headlen * Math.cos(angle + Math.PI / 6);
        let arrowY1 = y - headlen * Math.sin(angle + Math.PI / 6);
        lines.push({
            x1: x / this.state.canvasWidth,
            y1: y / this.state.canvasHeight,
            x2: arrowX1 / this.state.canvasWidth,
            y2: arrowY1 / this.state.canvasHeight
        });
        let arrowX2 = x - headlen * Math.cos(angle - Math.PI / 6);
        let arrowY2 = y - headlen * Math.sin(angle - Math.PI / 6);
        lines.push({
            x1: x / this.state.canvasWidth,
            y1: y / this.state.canvasHeight,
            x2: arrowX2 / this.state.canvasWidth,
            y2: arrowY2 / this.state.canvasHeight
        });

        this.setState({
            isDown: false,
            lines: lines
        });

    }
    drawArrow() {
        this.setState({
            arrow: true
        })
    }

    drawLine() {
        this.setState({
            arrow: false
        })
    }

    clearAll = () => {

        this.setState({
            previousPointX: "",
            previousPointY: "",
            lines: []
        })
        const canvas = this.refs.canvas;
        var ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.beginPath();

    }
    capture() {

        // let dataURL = this.state.dataURL;
        this.clearAll();

        var video = document.getElementById('video');
        var canvas = this.refs.canvas;
        canvas.getContext('2d').drawImage(video, 0, 0, 500, 278);

        this.setState({
            lines: [],
            // dataURL:dataURL
        })

    }

    drawLines() {
        const canvas = this.refs.canvas;
        var ctx = canvas.getContext("2d");
        this.state.lines.map(line => {
            ctx.moveTo(line.x1 * this.state.canvasWidth, line.y1 * this.state.canvasHeight);
            ctx.lineTo(line.x2 * this.state.canvasWidth, line.y2 * this.state.canvasHeight);
            ctx.stroke();
            ctx.strokeStyle = "red";
        });

    }

    render() {
        let isPlaying = this.state.isPlaying;
        let arrow = this.state.arrow;
        return (
            <div>

                <Card>
                    <Row>

                        <Col span={12}>

                            <br/><br/>&nbsp;&nbsp;<Button  onClick={()=>this.startFeed(this.props.location.feed.id)}><Icon type="play-circle" />Start Feed</Button>
                            &nbsp;&nbsp;<Button onClick={this.stopFeed}><Icon type="pause-circle" />Stop Feed</Button>
                            &nbsp;&nbsp;<Button
                                    onClick={this.capture}><Icon type="camera" />Capture</Button><br/><br/>
                            <Tag color="#f50">{this.props.location.feed.site} / {this.props.location.feed.location} / {this.props.location.feed.name}/ {this.props.location.feed.url}</Tag>
                            <br/><br/>
                            {isPlaying
                                ? <img style={{border:"1px solid black"}} id="video" controls width="500" height="260" src="http://localhost:9000/stream"></img>
                                :
                                <img style={{border:"1px solid black"}} id="video" controls width="500" height="260" src="http://localhost:9000/ss"></img>
                            }
                            <br/><br/><Button style={{width: "500px"}} type="primary"
                                              onClick={() => this.setState({isPlaying: !isPlaying})}>PLAY/PAUSE</Button>

                        </Col>

                        <Col  span={12}>
                            <br/><br/>

                            <h3>Draw by clicking & moving the mouse !</h3>
                            <canvas id="canvas" ref="canvas"
                                    style={{border:"1px solid black"}}
                                    width={500}
                                    height={278}
                                    border="2x solid blue"
                                    allowTaint="true"
                                    onMouseDown={
                                        e => {
                                            let nativeEvent = e.nativeEvent;
                                            this.handleMouseDown(nativeEvent);
                                        }}
                                    onMouseMove={
                                        e => {
                                            let nativeEvent = e.nativeEvent;
                                            this.handleMouseMove(nativeEvent);
                                        }}
                                    onMouseUp={
                                        e => {
                                            let nativeEvent = e.nativeEvent;
                                            if (arrow === false) {
                                                this.handleMouseUp(nativeEvent);
                                            } else {
                                                this.handleArrow(nativeEvent);
                                            }
                                        }}

                            />
                            <div align={"center"}>
                                <h4>Draw using:</h4><Button onClick={this.drawLine}><Icon type="line" />line</Button>
                                &nbsp;&nbsp;<Button onClick={this.drawArrow}><Icon type="arrow-up" /><Icon type="arrow-down" />arrow</Button><br/><br/>
                            </div>
                                <div align={"right"}> <Button block type={"primary"}  onClick={this.save}>Save</Button>
                                &nbsp;&nbsp;<Button block onClick={this.clearAll}>Clear All</Button><br/><br/>
                                </div>
                        </Col>
                    </Row>
                </Card>

            </div>
        );
    }


}