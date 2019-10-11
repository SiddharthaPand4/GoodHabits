import React, {Component} from "react";
import {Col, Row, Statistic} from "antd";

export default class HomeView extends Component {

    render() {
        return (
            <div>
                <Row gutter={16}>
                    <Col span={12}>
                        <Statistic title="Incidents (This Week)" value={24} />
                    </Col>
                    <Col span={12}>
                        <Statistic title="Incidents (Last Week)" value={34} />
                    </Col>
                </Row>
            </div>
        )
    }
}