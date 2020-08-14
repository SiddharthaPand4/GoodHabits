import React, {Component} from 'react'
import {Col, message, Row, Switch, Card, Button} from "antd";
import AlertService from "../../services/AlertService";

export default class AlertConfig extends Component {

    state = {
        alertTypes: []
    }

    componentDidMount() {
        this.fetchDataAndConnect()
    }

    fetchDataAndConnect = async ()=> {
        try {
            const alertTypesResponse = await AlertService.fetchAlertTypes();
            const alertTypes = alertTypesResponse.data;
            this.connections = []
            alertTypes.forEach(alert => {
                const con = new WebSocket(alert.url)
                con.onmessage = event => {
                    message.warn(`Hotlisted Vehicle ${event.data}, type: ${alert.text}`)
                }
                this.connections.push(con)
            })
        } catch (err) {
            message.error("Something Went Wrong")
            console.log(err)
        }
    }

    componentWillUnmount() {
        this.connections.map(con => {
            con.close()
        })
    }

    toggleAlert = (status, i) => {
        const alertTypes = [...this.state.alertTypes]
        alertTypes[i].status = status
        this.setState({alertTypes})
    }

    saveConfig = async () => {
        try {
            const res = await AlertService.saveAlertSettings({...this.state})
            console.log(res.response)
            message.success("Saved")
        } catch (e) {
            console.log(e)
            message.error("Something Went Wrong")
        }
    }

    render() {
        return (
            <div>
                <Card>
                    <h2>Hotlisted Vehicles Alert Settings</h2>
                    {this.state.alertTypes.map((type, i) =>
                        <Row dataIndex={i}>
                            <Col>
                                type.text
                            </Col>
                            <Col>
                                <Switch checked={type.status} onChange={status => this.toggleAlert(status, i)} />
                            </Col>
                        </Row>
                    )}
                    <Row>
                        <Col>
                            <Button onClick={this.saveConfig}>Save</Button>
                        </Col>
                    </Row>
                </Card>
            </div>
        )

    }

}