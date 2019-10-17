import React, {Component} from "react";
import {Card, Icon} from 'antd';
import DeviceService from "../services/DeviceService";
import {Link} from "react-router-dom";

export default class DeviceView extends Component {

    constructor(props) {
        super(props);

        this.state = {loading:true, device:{}}
    }

    componentDidMount() {
        DeviceService.getDevice().then(request => {
            this.setState({"device" : request.data, loading : false})
        },
        error =>{
            console.log("error");
        })
    }

    render() {
        let loading = this.state.loading;
        let device = this.state.device;

        if (loading) {
            return (<div>Loading..</div>)
        }

        return (

            <div>
                <Card title="Device details" style={{width: 300}} extra={<Link to="/device/conf"><Icon type="setting" /></Link>}>
                    <p>ID: {device.fingerprint}</p>
                    <p>Name: {device.name}</p>
                    <p>Model: {device.model}</p>
                    <p>License: {device.license}</p>
                    <p>Status: {device.status}</p>
                    <p>RegisteredTo: {device.registeredTo}</p>

                </Card>
            </div>
        )
    }
}