import React, {Component} from "react";
import {Button, Col, Row, Slider} from "antd";
import queryString from 'query-string';
import {Stage, Layer, Image, Rect, Text, Star} from 'react-konva';
import useImage from 'use-image';

const ParkingImage = () => {
    const [image] = useImage('/pgs/p-001.png');
    return <Image image={image} />;
};

const data = {
    "P1" :  {"free": true, x: 73,  y : 188},
    "P2" :  {"free": true, x: 102, y : 206},
    "P3" :  {"free": true, x: 131, y : 224},
    "P4" :  {"free": false, x: 160, y : 242},
    "P5" :  {"free": true, x: 189, y : 260},
    "P6" :  {"free": false, x: 218, y : 278},
    "P7" :  {"free": true, x: 247, y : 296},
    "P8" :  {"free": true, x: 276, y : 314},
    "P9" :  {"free": true, x: 305, y : 332},
    "P10" : {"free": false, x: 333, y : 354},
    "P11" : {"free": true, x: 152, y :  60},
    "P12" : {"free": true, x: 181, y :  78},
    "P13" : {"free": true, x: 210, y :  96},
    "P14" : {"free": true, x: 239, y : 114},
    "P15" : {"free": true, x: 268, y : 132},
    "P16" : {"free": false, x: 297, y : 150},
    "P17" : {"free": true, x: 326, y : 168},
    "P18" : {"free": true, x: 355, y : 186},
    "P19" : {"free": true, x: 384, y : 204},
    "P20" : {"free": false, x: 412, y : 226}
};

export default class PgsConsoleView extends Component {
    render() {

        let params = queryString.parse(this.props.location.search);

        return (

            <div>
                <Row>
                    <Col md={8}>
                        <Stage width={500} height={500}>
                            <Layer>
                                <ParkingImage/>
                                {Object.keys(data).map((k) => (
                                    <Star
                                        key={k}
                                        x={data[k].x}
                                        y={data[k].y}
                                        numPoints={20}
                                        innerRadius={5}
                                        outerRadius={10}
                                        fill={data[k].free ? "#89b717" : "red"}
                                        opacity={0.5}
                                        rotation={30}
                                    />
                                ))}
                            </Layer>
                        </Stage>
                    </Col>
                    <Col md={8}>
                        Car: <Slider defaultValue={5} tooltipVisible max={20} marks={[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]}/>
                        Bike: <Slider defaultValue={5} tooltipVisible max={0} disabled/>

                        {params.edit && <div><label>Toggle:</label><input/><Button>GO</Button></div>}
                    </Col>
                </Row>
            </div>
        )
    }
}