import React, {Component} from "react";
import {Button, Col, Row, Slider} from "antd";
import queryString from 'query-string';
import {Image, Layer, Line, Stage, Star} from 'react-konva';
import useImage from 'use-image';
import ApmsService from "../../services/ApmsService";

const ParkingImage = () => {
    const [image] = useImage('/public/apms/lot/lucknow/image.jpg');
    return <Image image={image}/>;
};

export default class ParkingConsoleView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            slots: {},
            loading: true,

            biketotal: 0,
            cartotal: 0,
            bikefull: 0,
            carfull: 0
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
            let biketotal = 0;
            let cartotal = 0;
            let bikefull = 0;
            let carfull = 0;

            response.data.forEach((v, i) => {
                switch (v.vehicleType) {
                    case "Bike":
                        biketotal++;
                        if (!v.free) bikefull++;
                        break;
                    case "Car":
                        cartotal++;
                        if (!v.free) carfull++;
                }
            });

            console.log(carfull,cartotal, bikefull, biketotal);
            this.setState({
                slots: response.data,
                loading: false,
                carfull: carfull,
                cartotal: cartotal,
                bikefull: bikefull,
                biketotal: biketotal
            })
        })
    }


    render() {
        const loading = this.state.loading;
        const data = this.state.slots;
        const biketotal = this.state.biketotal;
        const cartotal = this.state.cartotal;
        const bikefull = this.state.bikefull;
        const carfull = this.state.carfull;


        if (loading || !data) {
            return (<div>Loading...</div>)
        }

        console.log(this.state);
        let params = queryString.parse(this.props.location.search);

        return (

            <div>
                <Row>
                    <Col md={8}>
                        <Stage width={500} height={500}>
                            <Layer>
                                <ParkingImage/>
                                {Object.keys(data).map((k) => (
                                    <Line
                                        key={k}
                                        points={[data[k].p1x, data[k].p1y, data[k].p2x, data[k].p2y, data[k].p3x, data[k].p3y, data[k].p4x, data[k].p4y,]}
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
                        Car: <Slider defaultValue={carfull} tooltipVisible max={cartotal}/>
                        Bike: <Slider defaultValue={bikefull} tooltipVisible max={biketotal} />

                        {params.edit && <div><label>Toggle:</label><input/><Button>GO</Button></div>}
                    </Col>
                </Row>
            </div>
        )
    }
}