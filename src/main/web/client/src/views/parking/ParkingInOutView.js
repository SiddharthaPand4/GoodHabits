import React, {Component} from "react";
import {
    Card,
    Col,
    Icon,
    Row,
    Table,
    Input, Button, Spin
} from 'antd';

import Moment from "react-moment";
import moment from "moment";
import ApmsService from "../../services/ApmsService";

const {Column} = Table;
const {Search} = Input;
const antIcon = <Icon type="loading" style={{fontSize: 24}} spin/>;

export default class ParkingInOutView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: {
                checkIn: false,
                checkOut: false
            },

            vehicleLastParkingEvent: {},
            filter: {
                page: 1,
                pageSize: 10,
                loading: false,
                apmsresponse: {},
                vehicleNo: "",
            }
        };

        this.refresh = this.refresh.bind(this);
        this.copyVehicleNo = this.copyVehicleNo.bind(this);
        this.getEventsList = this.getEventsList.bind(this);
        this.addCheckIn = this.addCheckIn.bind(this);
        this.onLprInputChange = this.onLprInputChange.bind(this);
        this.addCheckOut = this.addCheckOut.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
    }

    refresh() {
        let filter = this.state.filter;
        filter.page = 1;
        this.getEventsList(filter);
        if (filter.vehicleNo && filter.vehicleNo.length > 2) {
            this.getEventsStatus(filter.vehicleNo);
        }
    }

    componentDidMount() {
        this.refresh();
    }


    onLprInputChange(e) {
        let filter = this.state.filter;
        filter.vehicleNo = e.target.value;
        this.setState({filter: filter});
        if (filter.vehicleNo && filter.vehicleNo.length > 2) {
            this.getEventsStatus(filter.vehicleNo);
        }
    }

    onPageChange(page, pageSize) {
        let filter = this.state.filter;
        filter.page = page;
        filter.pageSize = pageSize;
        this.getEventsList(filter)

    }

    onPageSizeChange(current, pageSize) {
        let filter = this.state.filter;
        filter.pageSize = pageSize;
        this.getEventsList(filter);
    }

    copyVehicleNo(vehicleNo) {
        let filter = this.state.filter;
        filter.vehicleNo = vehicleNo;
        this.setState({filter: filter});
        if (filter.vehicleNo && filter.vehicleNo.length > 2) {
            this.getEventsStatus(filter.vehicleNo);
        }
    }

    addCheckIn() {

        let vehicleNo = this.state.filter.vehicleNo;
        let {loading} = this.state;
        if (vehicleNo) {
            loading.checkIn = true;
            this.setState({loading});
            ApmsService.checkIn(vehicleNo).then(response => {
                this.refresh();
                loading.checkIn = false;
                this.setState({loading});
            }).catch(error => {
                loading.checkIn = false;
                this.setState({loading});
                alert("Something went wrong");
            })
        }
    }

    addCheckOut() {
        let vehicleNo = this.state.filter.vehicleNo;
        let {loading} = this.state;
        if (vehicleNo) {
            loading.checkOut = true;
            this.setState({loading});
            ApmsService.checkOut(vehicleNo).then(response => {
                loading.checkOut = false;
                this.setState({loading});
                this.refresh();
            }).catch(error => {
                loading.checkOut = false;
                this.setState({loading});
                alert("Something went wrong");
            })
        }
    }

    getEventsStatus(vehicleNo) {
        ApmsService.getEventStatus(vehicleNo).then(response => {
            this.setState({vehicleLastParkingEvent: response.data})
        }).catch(error => {
            alert("Something went wrong");
        });
    }

    getEventsList(filter) {

        filter.loading = true;
        this.setState({filter: filter});

        ApmsService.getEvents(filter).then(response => {
            filter.loading = false;
            filter.apmsresponse = response.data;
            for (var i in filter.apmsresponse.events) {
                filter.apmsresponse.events[i].timeDiff = this.getTimeDifference(filter.apmsresponse.events[i]);
            }
            this.setState({filter: filter});
        }).catch(error => {
            filter.loading = false;
            this.setState({hasError: true});
            alert("Something went wrong");
        });
    }

    getTimeDifference(record) {
        var a = moment(record.checkIn);
        var b = moment(record.checkOut);

        if (a & b) {
            return (moment.utc(moment(b, "DD/MM/YYYY HH:mm").diff(moment(a, "DD/MM/YYYY HH:mm"))).format("HH:mm"))
        }
    }

    isEmpty(obj) {

        // null and undefined are "empty"
        if (obj == null) return true;

        // Assume if it has a length property with a non-zero value
        // that that property is correct.
        if (obj.length > 0) return false;
        if (obj.length === 0) return true;

        // If it isn't an object at this point
        // it is empty, but it can't be anything *but* empty
        // Is it empty?  Depends on your application.
        if (typeof obj !== "object") return true;

        // Otherwise, does it have any properties of its own?
        // Note that this doesn't handle
        // toString and valueOf enumeration bugs in IE < 9
        for (let key in obj) {
            if (hasOwnProperty.call(obj, key)) return false;
        }
        return true;
    }

    getEventId() {
        let filter = {...this.state.filter};
        if (!filter.loading) {
            if (filter.apmsresponse.events) {
                if (filter.apmsresponse.events.length > 0) {
                    return parseInt(filter.apmsresponse.events[0].id);
                }
            }
        }
        return 0;
    }

    render() {

        let filter = this.state.filter;
        let events = this.state.filter.apmsresponse.events;
        let vehicleNo = this.state.filter.vehicleNo;
        let vehicleLastParkingEvent = this.state.vehicleLastParkingEvent;
        let loading = this.state.loading;
        let count = this.state.filter.apmsresponse.totalPages * this.state.filter.apmsresponse.pageSize;

        const paginationOption = {
            showSizeChanger: false,
            showQuickJumper: false,

            showLessItems: true,
            onShowSizeChange: this.onPageSizeChange,
            onChange: this.onPageChange,
            total: count
        };
        const pagination = {
            ...paginationOption,
            total: count,
            current: this.state.filter.page,
            pageSizes: this.state.filter.pageSize
        };

        let disableCheckIn = true;
        let disableCheckOut = true;

        if ((this.isEmpty(vehicleLastParkingEvent) || vehicleLastParkingEvent.checkOut) && vehicleNo) {
            disableCheckIn = false;
        }
        if ((!(this.isEmpty(vehicleLastParkingEvent)) && (!vehicleLastParkingEvent.checkOut)) && vehicleNo) {
            disableCheckOut = false;

        }

        return (

            <div>
                <h3>Parking Event</h3>
                <Card>
                    <Row>
                        <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>

                            <img width="440" height="260"
                                 src={"/public/apms/parking/event/" + vehicleLastParkingEvent.id + "/image.jpg"}/>

                        </Col>

                        <Col xl={{span: 12}} lg={{span: 12}} md={{span: 12}} sm={{span: 24}} xs={{span: 24}}>

                            <Search allowClear
                                    value={vehicleNo}
                                    placeholder="Enter Vehicle No."
                                    enterButton="Search"
                                    onChange={this.onLprInputChange}
                                    size="large"

                                    onSearch={() => this.onPageChange(1, filter.pageSize)}

                            /><br/><br/><br/>


                            <Button type="primary" size="large" block="false" icon={"arrow-down"}
                                    loading={loading.checkIn} disabled={disableCheckIn}
                                    onClick={() => this.addCheckIn()}>
                                Entry
                            </Button><br/><br/><br/>


                            <Button type="primary" size="large" block="false" icon={"arrow-up"}
                                    loading={loading.checkOut} disabled={disableCheckOut}
                                    onClick={() => this.addCheckOut()}>
                                Exit
                            </Button>
                        </Col>
                    </Row>
                    <br/>
                </Card>


                {
                    this.state.filter.loading
                        ? <Spin indicator={antIcon}/>
                        : <div>

                            <div>
                                <Table dataSource={events} pagination={pagination}>

                                    <Column title="Vehicle no." dataIndex="vehicleNo" key="vehicleNo"

                                            render={vehicleNo => (<Button
                                                onClick={() => this.copyVehicleNo(vehicleNo)}>{vehicleNo}</Button>)}/>
                                    <Column title="Entry-time" dataIndex="checkIn" key="checkIn"
                                            render={checkIn => (<div>
                                                <div><Moment format="LLL">{checkIn}</Moment></div>
                                                <Moment fromNow>{checkIn}</Moment></div>)}/>
                                    <Column title="Exit-time" dataIndex="checkOut" key="checkOut"
                                            render={checkOut => (checkOut ? <div>
                                                    <div>
                                                        <Moment format="LLL">{checkOut}</Moment>
                                                    </div>
                                                    <Moment fromNow>{checkOut}</Moment>
                                                </div> : ""
                                            )}
                                    />
                                    <Column title="Time-diff(HH:mm)" render={(text, record) => (record.timeDiff)}/>
                                </Table>
                            </div>
                        </div>
                }

            </div>
        )
    }

}


