import React, {Component} from "react";
import {Button, Col, Row, Select, Slider, Card} from "antd";
import queryString from 'query-string';
import {Image, Layer, Line, Stage, Star, Group, Label, Tag, Text} from 'react-konva';
import useImage from 'use-image';
import ApmsService from "../../services/ApmsService";

const {Option} = Select;

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
        };
        this.togglerRef = React.createRef();
    }

    componentDidMount() {
        this.intervalID = setInterval(this.refresh.bind(this), 30 * 1000);
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


            this.setState({
                slots: response.data,
                loading: false,
                carfull: carfull,
                cartotal: cartotal,
                bikefull: bikefull,
                biketotal: biketotal
            });
            let params = queryString.parse(this.props.location.search);
            if (params.edit) {
                this.togglerRef.current.refresh(response.data)
            }
        })
    }


    render() {
        const loading = this.state.loading;
        const data = this.state.slots;
        const biketotal = this.state.biketotal;
        const cartotal = this.state.cartotal;
        const bikefull = this.state.bikefull;
        const carfull = this.state.carfull;

        console.log("R", carfull, cartotal, bikefull, biketotal);
        if (loading || !data) {
            return (<div>Loading...</div>)
        }

        let params = queryString.parse(this.props.location.search);

        return (

            <div>
                <Row>
                    <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>
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
                                    <Group>
                                        <Label x={data[k].x}
                                               y={data[k].y}

                                        >
                                            <Tag

                                                fill='black'
                                                pointerDirection='down'
                                                pointerWidth={10}
                                                pointerHeight={10}
                                                lineJoin='round'
                                                shadowColor='black'
                                            />
                                            <Text

                                                text={data[k].name}
                                                fontFamily='Calibri'
                                                fontSize={12}
                                                padding={3}
                                                fill='white'
                                            />
                                        </Label>
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
                                    </Group>
                                ))}
                            </Layer>
                        </Stage>
                    </Col>
                    <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>
                        <Card>

                            Car: <Slider value={carfull} tooltipVisible max={cartotal}/>
                            Bike: <Slider value={bikefull} tooltipVisible max={biketotal}/>

                            {params.edit && <SlotToggler ref={this.togglerRef} slots={data}/>}
                        </Card>

                    </Col>
                </Row>
            </div>
        )
    }
}

class SlotToggler extends Component {
    constructor(props) {
        super(props);

        let slotmap = {};
        props.slots.forEach(v => {
            slotmap[v.name] = v;
        });

        this.state = {
            slots: slotmap,
            selectedSlot: "C1"
        };
        this.handleChange = this.handleChange.bind(this);
        this.updateSlot = this.updateSlot.bind(this);
    }

    refresh(data) {
        let slotmap = {};
        data.forEach(v => {
            slotmap[v.name] = v;
        });

        this.setState({
            slots: slotmap,
        });
    }

    handleChange(value) {
        this.setState({selectedSlot: value});
    }

    updateSlot() {
        ApmsService.updateSlot(this.state.selectedSlot, !this.state.slots[this.state.selectedSlot].free)
    }

    render() {
        const ss = this.state.selectedSlot;
        const slots = this.props.slots;
        return (
            <div>
                <label>Toggle:</label>
                <Select onChange={this.handleChange} defaultValue={ss}>

                    {(slots || []).map((slot, index) => {
                        return <Option value={slot.name}>{slot.name}</Option>
                    })}

                </Select>
                <Button onClick={this.updateSlot}>GO</Button></div>)
    }
}