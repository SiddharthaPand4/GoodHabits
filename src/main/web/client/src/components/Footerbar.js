import React, {Component} from 'react';
import {Layout} from "antd";

const {Footer} = Layout;

export default class Headbar extends Component {

    render() {
        return (
            <Footer style={{textAlign: 'center'}}>
                Powered by SynergyLabs © 2019</Footer>
        )
    }
}