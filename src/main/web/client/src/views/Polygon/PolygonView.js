import React, {Component} from "react";

import Canvas from "./Canvas";
import RegionsList from "./RegionsList";
import {Col, Row} from "antd";



export default class PolygonView extends Component {

    render() {




        return (
            <React.Fragment>
                <h2>Image prototype</h2>
                <p className="description">Draw objects conturs on top of the image</p>
                <div className="PolygonView">
                    <Row>

                        <Col span={16}>
                            <div className="right-panel">

                                <Canvas/>
                            </div>

                        </Col>
                        <Col span={16}>
                            <div className="left-panel">

                                <RegionsList/>
                            </div>
                        </Col>
                    </Row>


                </div>
            </React.Fragment>
        );
    }
}