import React, {Component} from 'react'
import {Col, message, Row, Switch, Card} from "antd";
import AlertService from "../../services/AlertService";

export default class AlertConfig extends Component {

    state = {
        alertTypes: []
    }

    componentDidMount() {
        this.fetchDataAndConnect()
    }

    fetchDataAndConnect = async ()=> {
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
                </Card>
            </div>
        )

    }

}