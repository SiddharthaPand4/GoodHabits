import React, {Component} from 'react';
import {Layout, Row, Col} from "antd";

const {Footer} = Layout;

export default class Headbar extends Component {

    render() {
        return (
            <Footer>
                <Row>
                    <Col span={12} style={{textAlign: 'left'}}><span >Provided by SynergyLabs Technology</span></Col>
                    <Col span={12} style={{textAlign: 'right'}}><span>Powered by {this.props.org.legalName}</span></Col>
                </Row>
            </Footer>
        )
    }
}