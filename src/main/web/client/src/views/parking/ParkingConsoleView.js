import React, {Component} from "react";
import {Button, Col, Row, Select, Switch} from "antd";
import queryString from 'query-string';
import {Group, Image, Label, Layer, Line, Stage, Star, Tag, Text} from 'react-konva';
import ApmsService from "../../services/ApmsService";

const {Option} = Select;

class ParkingImage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            image: null
        };
    }

    componentDidMount() {
        this.loadImage();
    }

    componentDidUpdate(oldProps) {

        if (oldProps.src !== this.props.src) {
            this.loadImage();
        }
    }
    componentWillUnmount() {
        this.image.removeEventListener('load', this.handleLoad);
    }

    handleLoad = () => {
        // after setState react-konva will update canvas and redraw the layer
        // because "image" property is changed
        this.setState({
            image: this.image
        });
        // if you keep same image object during source updates
        // you will have to update layer manually:
        // this.imageNode.getLayer().batchDraw();
    };

    loadImage() {

        // save to "this" to remove "load" handler on unmount
        this.image = new window.Image();
        this.image.src = this.props.src;
        this.image.addEventListener('load', this.handleLoad);
    }

    render() {

        return (
            <Image
                x={this.props.x}
                y={this.props.y}
                image={this.state.image}
                ref={node => {
                    this.imageNode = node;
                }}
            />
        );
    }
}

export default class ParkingConsoleView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            slots: {},
            loading: true,

            biketotal: 0,
            cartotal: 0,
            bikefull: 0,
            carfull: 0,
            img: '/public/apms/lot/lucknow/image.jpg',
            baseimg: '/public/apms/lot/lucknow/image.jpg'
        };
        this.togglerRef = React.createRef();
    }

    componentDidMount() {
        this.intervalID = setInterval(this.refresh.bind(this), 30 * 1000);
        this.ptTimer = setInterval(this.setOccupied.bind(this), 1000);
        this.refresh();
    }

    setOccupied() {
        let slots = this.state.slots;
        slots.forEach(v => {
            if (!v.free) {
                v.lastOccupiedSeconds++
            }
        });
        this.setState({slots:slots});
    }

    componentWillUnmount() {
        clearInterval(this.intervalID);
        clearInterval(this.ptTimer);
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
                biketotal: biketotal,
                img: this.state.baseimg + "?rng=" + Math.random()
            });
            let params = queryString.parse(this.props.location.search);
            if (params.edit) {
                this.togglerRef.current.refresh(response.data)
            }
        })
    }

    getText(slot) {

        if (slot.free)
            return slot.misaligned ? (slot.name + " (Misaligned)") : slot.name;

        return slot.misaligned ? (slot.name + " (" + this.fancyTime(slot.lastOccupiedSeconds) + ") " + " (Misaligned)") : slot.name + " (" + this.fancyTime(slot.lastOccupiedSeconds) + ") ";
    }

    fancyTime(secs) {
        if (secs <= 60)
            return secs + "s";
        if (secs <= 3600)
            return Math.floor(secs / 60) +"m" + (secs % 60) + "s";
    }
    render() {
        const loading = this.state.loading;
        const data = this.state.slots;
        const biketotal = this.state.biketotal;
        const cartotal = this.state.cartotal;
        const bikefull = this.state.bikefull;
        const carfull = this.state.carfull;
        const img = this.state.img;

        console.log("R", carfull, cartotal, bikefull, biketotal);
        if (loading || !data) {
            return (<div>Loading...</div>)
        }

        let params = queryString.parse(this.props.location.search);

        return (

            <div>
                <Row>
                    <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>
                        <Stage width={1280} height={724}>
                            <Layer>
                                <ParkingImage src={img}/>
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

                                                fill={'black'}
                                                pointerDirection='down'
                                                pointerWidth={10}
                                                pointerHeight={10}
                                                lineJoin='round'
                                                shadowColor={'black'}
                                            />
                                            <Text

                                                text={this.getText(data[k])}
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
                                            fill={data[k].free ? "green" : (data[k].misaligned ? 'blue' : 'red')}
                                            opacity={1.0}
                                            rotation={30}
                                        />
                                    </Group>
                                ))}
                            </Layer>
                        </Stage>
                    </Col>
                    {/*
                    <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>
                        <Card>

                            Car: <Slider value={carfull} tooltipVisible max={cartotal}/>
                            Bike: <Slider value={bikefull} tooltipVisible max={biketotal}/>


                        </Card>

                    </Col>
                    */}
                </Row>
                <Row>
                    <Col>
                        {params.edit && <SlotToggler ref={this.togglerRef} slots={data}/>}
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
            selectedSlot: "C1",
            ss: slotmap["C1"]
        };
        this.handleChange = this.handleChange.bind(this);
        this.updateSlot = this.updateSlot.bind(this);
        this.onAlignmentChange = this.onAlignmentChange.bind(this);
        this.onFreeChange = this.onFreeChange.bind(this);
    }

    refresh(data) {
        let slotmap = {};

        data.forEach(v => {
            slotmap[v.name] = v;
        });
        let ss = slotmap[this.state.selectedSlot];
        this.setState({
            slots: slotmap,
            ss: ss
        });
    }

    handleChange(value) {
        let slot = this.state.slots[this.state.selectedSlot];
        this.setState({selectedSlot: value, ss: slot});
    }

    onAlignmentChange(value) {
        let slot = this.state.ss;
        slot.misaligned = value;
        this.setState({ss: slot});
    }

    onFreeChange(value) {
        let slot = this.state.ss;
        slot.free = value;
        this.setState({ss: slot});
    }

    updateSlot() {
        ApmsService.updateSlot(this.state.ss)
    }

    render() {
        const ss = this.state.ss;
        const slots = this.props.slots;
        return (
            <div>
                <table>
                    <tr>
                        <th>Slot</th>
                        <th>Free</th>
                        <th>MisAligned</th>
                        <th>&nbsp;</th>
                    </tr>
                    <tr>
                        <td>
                            <Select onChange={this.handleChange} value={ss.name}>

                                {(slots || []).map((slot, index) => {
                                    return <Option value={slot.name}>{slot.name}</Option>
                                })}

                            </Select>
                        </td>
                        <td>
                            <Switch checked={ss.free} onChange={this.onFreeChange}/>
                        </td>
                        <td>
                            <Switch checked={ss.misaligned} onChange={this.onAlignmentChange}/>
                        </td>
                        <td><Button onClick={this.updateSlot}>GO</Button></td>
                    </tr>
                </table>
            </div>
        )
    }
}
