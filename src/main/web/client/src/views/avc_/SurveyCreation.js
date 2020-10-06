import React, {Component} from 'react'
import {Button, Card, Input, message, Col, Row, DatePicker} from 'antd'
import AvcService from "../../services/AvcService";

const {RangePicker} = DatePicker

export default class SurveyCreation extends Component {

    state = {
        survey : {
            id: 0,
            name: "",
            folder: "",
            startDate: "",
            endDate: ""
        }
    }

    handleFolderChange = e => {
        const folder = e.target.value
        const survey = {...this.state.survey, folder}
        this.setState({survey})
    }

    handleNameChange = e => {
        const name = e.target.value
        const survey = {...this.state.survey, name}
        this.setState({survey})
    }

    onRangeChange = (_, dates) => {
        const startDate = dates[0]
        const endDate = dates[1]
        const survey = {...this.state.survey, startDate, endDate}
        this.setState({survey})
    }

    validate = () => {
        let valid = true
        let error = ""
        if (this.state.survey.name.length < 3 || this.state.survey.name.length > 30) {
            error = "Survey Name should be between 3 to 30 characters"
            valid = false
        } else if (this.state.survey.folder.length < 3 || this.state.survey.folder.length > 30) {
            error = "Folder Name should be between 3 to 30 characters"
            valid = false
        } else if (!(this.state.survey.startDate && this.state.survey.endDate)) {
            error = "Date Range Not Entered"
            valid = false
        }
        if (!valid) message.error(error)
        return valid
    }

    saveSurvey = async () => {
        if (this.validate()) {
            try {
                await AvcService.saveSurvey({...this.state.survey})
                message.success("Survey Created!")
            } catch (e) {
                message.error("Something Went Wrong!")
                console.log(e)
            }
        }
    }

    render() {
        return (
            <div>
                <h1 align={"center"} style={{margin: '40px'}}>Create Survey</h1>
                <Card bordered={false}>
                    <Row>
                        <Col offset={7} span={11}>
                            <Card type={"small"} bordered={false}>
                                <b>Enter Name: </b>
                                <Input
                                    value={this.state.survey.name}
                                    onChange={this.handleNameChange}
                                    placeholder={"Survey Name"}
                                />
                            </Card>
                        </Col>
                    </Row>
                    <Row>
                        <Col offset={7} span={11}>
                            <Card type={"small"} bordered={false}>
                                <b>Enter Folder: </b>
                                <Input
                                    value={this.state.survey.folder}
                                    onChange={this.handleFolderChange}
                                    placeholder={"Survey Folder"}
                                />
                            </Card>
                        </Col>
                    </Row>
                    <Row>
                        <Col offset={8} span={11}>
                            <Card type={"small"} bordered={false}>
                                <b>Date Range: </b><RangePicker showTime onChange={this.onRangeChange} />
                            </Card>
                        </Col>
                    </Row>
                    <Row>
                        <Col offset={11}>
                            <Card bordered={false} type={"small"}>
                                <Button type={"primary"} onClick={this.saveSurvey}>
                                    Save
                                </Button>
                            </Card>
                        </Col>
                    </Row>
                </Card>
            </div>
        )
    }

}