import React, {Component} from 'react'
import {Col, message, Row, Switch, Card, Button} from "antd";
import AlertService from "../../services/AlertService";

export default class AlertConfig extends Component {

    state = {
        alertTypes: []
    }

    componentDidMount() {
        this.fetchAlertTypes()
    }

    fetchAlertTypes = async ()=> {
        try {
            const alertTypesResponse = await AlertService.fetchAlertTypes();
            const alertTypes = alertTypesResponse.data;
            this.setState({alertTypes})
        } catch (err) {
            message.error("Something Went Wrong")
            console.log(err)
        }
    }

    toggleAlert = (status, i) => {
        const alertTypes = [...this.state.alertTypes]
        alertTypes[i].status = status
        this.setState({alertTypes})
    }

    saveConfig = async () => {
        try {
            await AlertService.saveAlertSettings(this.state.alertTypes)
            message.success("Saved")
        } catch (e) {
            console.log(e)
            message.error("Something Went Wrong")
        }
    }

    render() {
        return (
            <div style={{padding:"40px"}}>
                <h1 align={"center"}>Hotlisted Vehicles Alert Settings</h1>
                <Card>
                    {this.state.alertTypes.map((type, i) =>
                        <Card type={"small"} bordered={false}>
                            <Row>
                                <Col offset={7} span={8}>
                                    {type.alertType}
                                </Col>
                                <Col>
                                    <Switch checked={type.status} onChange={status => this.toggleAlert(status, i)} />
                                </Col>
                            </Row>
                        </Card>
                    )}
                </Card>
                <Card bordered={false}>
                    <Row>
                        <Col offset={11}>
                            <Button type={"primary"} onClick={this.saveConfig}>Save</Button>
                        </Col>
                    </Row>
                </Card>
            </div>
        )

    }

}