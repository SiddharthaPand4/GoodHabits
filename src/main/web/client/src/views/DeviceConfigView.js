import React, {Component} from "react";
import DeviceService from "../services/DeviceService";
import { Tabs } from 'antd';

const { TabPane } = Tabs;

export default class DeviceConfigView extends Component {

    constructor(props) {
        super(props);
        this.state = {loading:true, deviceconfig:{}}
    }

    componentDidMount() {
        DeviceService.getDeviceConfig().then(request => {
            this.setState({"deviceconfig" : request.data, loading : false})
        })
    }

    onTabChange(tab) {
        console.log("tab changed", tab)
    }

    render() {
        return (
            <Tabs defaultActiveKey="1" onChange={this.onTabChange}>
                <TabPane tab="Feed" key="common">
                    Feed details
                </TabPane>
                <TabPane tab="Basic Intrusion" key="bi">
                    Basic Intrusion
                </TabPane>
                <TabPane tab="Advanced Intrusion" key="ai">
                    Advanced Intrusion
                </TabPane>
            </Tabs>
        )
    }
}