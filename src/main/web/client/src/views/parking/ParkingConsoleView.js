import React, {Component} from "react";
import {Button, Col, Row, Slider} from "antd";
import queryString from 'query-string';
import {Stage, Layer, Image, Rect, Text, Line, Star} from 'react-konva';
import useImage from 'use-image';
import ApmsService from "../../services/ApmsService";

const ParkingImage = () => {
    const [image] = useImage('/pgs/p-001.png');
    return <Image image={image} />;
};

export default class ParkingConsoleView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            slots : {},
            loading: true
        }
    }

    componentDidMount() {
        this.intervalID = setInterval(this.refresh.bind(this), 5000);
        this.refresh();
    }

    componentWillUnmount() {
        clearInterval(this.intervalID);
    }

    refresh() {

        ApmsService.getSlots().then(response => {

            this.setState({slots:response.data, loading:false})
        })
    }


    render() {
        const loading = this.state.loading;
        const data = this.state.slots;
        console.log(this.state.slots);

        if (loading || !data) {
            return (<div>Loading...</div>)
        }

        let params = queryString.parse(this.props.location.search);

        return (

            <div>
                <Row>
                    <Col md={8}>
                        <Stage width={500} height={500}>
                            <Layer>
                                <ParkingImage/>
                                {Object.keys(data).map((k) => (
                                    <Line points={[data[k].p1x, data[k].p1y, data[k].p2x, data[k].p2y,data[k].p3x, data[k].p3y,data[k].p4x, data[k].p4y,]}
                                          stroke="red" closed={true}/>
                                ))}
                                {Object.keys(data).map((k) => (
                                    <Star
                                        key={k}
                                        x={data[k].x}
                                        y={data[k].y}
                                        numPoints={20}
                                        innerRadius={10}
                                        outerRadius={10}
                                        fill={data[k].free ? "green" : "red"}
                                        opacity={1.0}
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