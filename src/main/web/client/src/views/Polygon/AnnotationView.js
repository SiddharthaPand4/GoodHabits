import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Button, Col, message} from "antd";
import AnnotationService from "../../services/AnnotationService";
import Row from "antd/es/grid/row";
import Card from "@material-ui/core/Card";


export default class AnnotationView extends Component {
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

    componentDidUpdate(prevProps, prevState, snapshot) {
        this.drawLines();
    }

    drawLines() {
        const canvas = this.refs.canvas;
        var ctx = canvas.getContext("2d");
        this.state.lines.map(line => {
            ctx.moveTo(line.x1 * this.state.canvasWidth, line.y1 * this.state.canvasHeight);
            ctx.lineTo(line.x2 * this.state.canvasWidth, line.y2 * this.state.canvasHeight);
            ctx.stroke();
        });

    }

    startFeed = () => {
        AnnotationService.startFeed().then(res => {
            message.success("Feed started")
            //this.history.push("/canvasview")

        }).catch(err => {
            message.error("Something Went Wrong!")
        })
    }
    stopFeed = () => {
        AnnotationService.stopFeed().then(res => {
            message.success("Feed stoped")
            //this.history.push("/canvasview")
        }).catch(err => {
            message.error("Something Went Wrong!")
        })
    }

    render() {
        let isPlaying = this.state.isPlaying;
        let arrow = this.state.arrow;
        return (
            <div>

                <Card>
                    <Row>
                        <Col span={12}>
                            {isPlaying
                                ? <img id="video" controls width="500" height="260" src="/pgs/p-001.png"></img>
                                :
                                <img id="video" controls width="500" height="260" src="http://localhost:9000/ss"></img>
                            }
                            <br/><br/><Button style={{width: "500px"}} type="primary"
                                              onClick={() => this.setState({isPlaying: !isPlaying})}>PLAY/PAUSE</Button>
                            <br/><br/><Button style={{width: "500px"}} type="primary" block
                                              onClick={this.capture}>Capture</Button>
                        </Col>

                        <Col span={12}>
                            <canvas id="canvas" ref="canvas"
                                    width={500}
                                    height={278}

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
                        </Col>
                    </Row>
                </Card>

                <div align={"right"}>
                    <Button onClick={this.drawLine}>line</Button>
                    <Button onClick={this.drawArrow}>arrow</Button>
                    <Button onClick={this.clearAll}>Clear All</Button>
                    <Button onClick={this.save}>Save</Button></div>
                <Button onClick={this.startFeed}>Start Feed</Button>
                <Button onClick={this.stopFeed}>Stop Feed</Button>

            </div>
        );
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

        // var canvas = ReactDOM.findDOMNode(this.refs.canvas),
        // ctx = canvas.getContext("2d");
//
        // canvas.width = this.state.canvasWidth;
        // canvas.height = this.state.canvasHeight;
//
//
        // var background = new Image();
        // background.src = "/pgs/p-001.png";
//
// Make// sure the image is loaded first otherwise nothing will draw.
        // background.onload = function () {
        //     ctx.drawImage(background, 0, 0);
        // }
    }


    handleMouseDown(event) { //added code here
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
            // ctx.moveTo(x, y);
            //
            // ctx.lineTo(x + 1, y + 1);
            // ctx.stroke();
        })
    }

    handleMouseMove(event) {
        let isDown = this.state.isDown;
        if (isDown === true) {


        }
    }

    handleMouseUp(event) {
//if(this.state.isDown){
        var x = event.offsetX;
        var y = event.offsetY;

        const canvas = this.refs.canvas;
        var ctx = canvas.getContext("2d");

        //for simple line
        let prevX = this.state.previousPointX;
        let prevY = this.state.previousPointY;
        let len = Math.sqrt(Math.pow(x - prevX, 2) + Math.pow((y - prevY), 2));
        if (len < 30) {
            //ctx.clearRect(0, 0, canvas.width, canvas.height);
            //ctx.beginPath();
            return;
        }

        // ctx.moveTo(prevX, prevY);
        // ctx.lineTo(x, y);

        // //common
        // ctx.stroke();
        // ctx.strokeStyle = "#FF0000";
        // ctx.closePath();

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
            //ctx.clearRect(0, 0, canvas.width, canvas.height);
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
        // for arrow
        // ctx.beginPath();
        // ctx.moveTo(x2, y2);
        // ctx.lineTo(x, y);

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
        //  ctx.lineTo(arrowX1, arrowY1);

        //  ctx.moveTo(x, y);

        //  ctx.lineTo(arrowX2, arrowY2)

        //  //common
        //  ctx.stroke();
        //  ctx.strokeStyle = "#FF0000";
        //  ctx.closePath();

        this.setState({
            isDown: false,
            lines: lines
        });
//}
    }

    capture() {

        let dataURL = this.state.dataURL;
        this.clearAll();
        //var canvas = document.getElementById('canvas');
        var video = document.getElementById('video');
        var canvas = this.refs.canvas;
        canvas.getContext('2d').drawImage(video, 0, 0, 500, 278);
        //this.clearAll();
        // dataURL = canvas.toDataURL();
        this.setState({
            lines: [],
            // dataURL:dataURL
        })


    }

    componentDidMount() {
        this.drawLines();
    }
}