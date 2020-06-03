import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Button} from "antd";

export default class PolygonView extends Component {
    constructor(props) {
        super(props);
//added state
        this.state = {
            arrow: false,
            isDown: false,
            previousPointX: '',
            previousPointY: ''
        }
        this.handleMouseDown = this.handleMouseDown.bind(this);
        this.handleMouseMove = this.handleMouseMove.bind(this);
        this.handleMouseUp = this.handleMouseUp.bind(this);
        this.handleArrow = this.handleArrow.bind(this);
        this.drawArrow = this.drawArrow.bind(this);
        this.drawLine = this.drawLine.bind(this);
    }

    render() {
        let arrow = this.state.arrow;
        return (
            <div>
                <canvas id="canvas" ref="canvas"
                        width={640}
                        height={425}

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
                <Button onClick={this.drawLine}>line</Button>
                <Button onClick={this.drawArrow}>arrow</Button>
                <Button onClick={this.clearAll}>Clear All</Button>
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
        const canvas = ReactDOM.findDOMNode(this.refs.canvas);
        var ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        // var canvas = ReactDOM.findDOMNode(this.refs.canvas),
        ctx = canvas.getContext("2d");

        canvas.width = 700;
        canvas.height = 400;


        var background = new Image();
        background.src = "/pgs/p-001.png";

// Make sure the image is loaded first otherwise nothing will draw.
        background.onload = function () {
            ctx.drawImage(background, 0, 0);
        }
    }




    handleMouseDown(event) { //added code here
        console.log(event);
        this.setState({
            isDown: true,
            previousPointX: event.offsetX,
            previousPointY: event.offsetY
        }, () => {
            const canvas = ReactDOM.findDOMNode(this.refs.canvas);
            var x = event.offsetX;
            var y = event.offsetY;
            var ctx = canvas.getContext("2d");
            // var headlen = 20;
            // var x2=this.state.previousPointX;
            // var y2=this.state.previousPointY;

            // var dx = event.offsetX - x2;
            // var dy = event.offsetY - y2;
            // var angle = Math.atan2(dy, dx);
            // ctx.moveTo(x2, y2);
            // ctx.lineTo(x, y);
            // //ctx.lineTo(x - headlen * Math.cos(angle - Math.PI / 6), y - headlen * Math.sin(angle - Math.PI / 6));
            // ctx.lineTo(x - headlen * Math.cos(angle + Math.PI / 6), y - headlen * Math.sin(angle + Math.PI / 6));
            // ctx.moveTo(x, y);
            // //ctx.lineTo(x - headlen * Math.cos(angle + Math.PI / 6), y - headlen * Math.sin(angle + Math.PI / 6));
            // ctx.lineTo(x - headlen * Math.cos(angle - Math.PI / 6), y - headlen * Math.sin(angle - Math.PI / 6));
            console.log(x, y);
            ctx.moveTo(x, y);
            ctx.lineTo(x + 1, y + 1);
            ctx.stroke();
        })
    }

    handleMouseMove(event) {
    }

    handleMouseUp(event) {
        this.setState({
            isDown: false
        });
//if(this.state.isDown){
        const canvas = ReactDOM.findDOMNode(this.refs.canvas);
        var x = event.offsetX;
        var y = event.offsetY;
        var ctx = canvas.getContext("2d");

        var headlen = 20;
        var x2 = this.state.previousPointX;
        var y2 = this.state.previousPointY;
        var dx = event.offsetX - x2;
        var dy = event.offsetY - y2;
        var angle = Math.atan2(dy, dx);
        // for arrow
        // ctx.moveTo(x2, y2);
        // ctx.lineTo(x, y);
//ctx.l//ineTo(x - headlen * Math.cos(angle - Math.PI / 6), y - headlen * Math.sin(angle - Math.PI / 6));
        // ctx.lineTo(x - headlen * Math.cos(angle + Math.PI / 6), y - headlen * Math.sin(angle + Math.PI / 6));
        // ctx.moveTo(x, y);
//ctx.l//ineTo(x - headlen * Math.cos(angle + Math.PI / 6), y - headlen * Math.sin(angle + Math.PI / 6));
        // ctx.lineTo(x - headlen * Math.cos(angle - Math.PI / 6), y - headlen * Math.sin(angle - Math.PI / 6))
        //for simple line
        ctx.moveTo(this.state.previousPointX, this.state.previousPointY);
        ctx.lineTo(x, y);

        //common
        ctx.stroke();
        ctx.strokeStyle = "#FF0000";
        ctx.closePath();
//}
    }

    handleArrow(event) {
        this.setState({
            isDown: false
        });
//if(this.state.isDown){
        const canvas = ReactDOM.findDOMNode(this.refs.canvas);
        var x = event.offsetX;
        var y = event.offsetY;
        var ctx = canvas.getContext("2d");

        var headlen = 20;
        var x2 = this.state.previousPointX;
        var y2 = this.state.previousPointY;
        var dx = event.offsetX - x2;
        var dy = event.offsetY - y2;
        var angle = Math.atan2(dy, dx);
        // for arrow
        ctx.beginPath();
        ctx.moveTo(x2, y2);
        ctx.lineTo(x, y);
//ctx.lineTo(x - headlen * Math.cos(angle - Math.PI / 6), y - headlen * Math.sin(angle - Math.PI / 6));
        ctx.lineTo(x - headlen * Math.cos(angle + Math.PI / 6), y - headlen * Math.sin(angle + Math.PI / 6));
        ctx.moveTo(x, y);
//ctx.lineTo(x - headlen * Math.cos(angle + Math.PI / 6), y - headlen * Math.sin(angle + Math.PI / 6));
        ctx.lineTo(x - headlen * Math.cos(angle - Math.PI / 6), y - headlen * Math.sin(angle - Math.PI / 6))
        //for simple line
        // ctx.moveTo(this.state.previousPointX, this.state.previousPointY);
        // ctx.lineTo(x, y);

        //common
        ctx.stroke();
        ctx.strokeStyle = "#FF0000";
        ctx.closePath();

//}
    }


    componentDidMount() {
        //  const canvas = ReactDOM.findDOMNode(this.refs.canvas);
        //  const ctx = canvas.getContext("2d");
        //  ctx.fillStyle = 'rgb(200,255,255)';
        //  ctx.fillRect(0, 0, 640, 425);
        var canvas = ReactDOM.findDOMNode(this.refs.canvas),
            ctx = canvas.getContext("2d");

        canvas.width = 700;
        canvas.height = 400;


        var background = new Image();
        background.src = "/pgs/p-001.png";

// Make sure the image is loaded first otherwise nothing will draw.
        background.onload = function () {
            ctx.drawImage(background, 0, 0);
        }
    }
}