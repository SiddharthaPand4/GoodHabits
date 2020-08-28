import React, {Component} from 'react'
import {Button, Card, Input, Upload, message, Col, Row} from "antd"
import {UploadOutlined} from '@ant-design/icons'
import OrgService from "../../services/OrgService";

export default class EditCurrentOrg extends Component {

    state = {
        org: {
            id: 0,
            name: "",
            legalName: "",
            logoFile: ""
        }
    }

    componentDidMount() {
        this.refresh()
    }

    refresh = () => {
        this.loadCurrentOrg()
    }

    loadCurrentOrg = async () => {
        try {
            const res = await OrgService.getOrgDetails()
            const org = {...res.data}
            this.setState({org})
        } catch (e) {
            message.error("Something Went Wrong")
            console.log(e)
        }
    }


    validate = () => {
        let valid = true
        let error = ""
        if (this.state.org.name.length < 3 || this.state.org.name > 30) {
            error = "Org Name should be between 3 to 30 charcaters"
            valid = false
        } else if (this.state.org.legalName.length < 3 || this.state.org.legalName > 30) {
            error = "Legal Name should be between 3 to 30 charcaters"
            valid = false
        } else if (!this.state.org.logoFile) {
            error = "Logo not uploaded"
            valid = false
        }
        if (!valid) message.error(error)
        return valid
    }

    validateUpload = logoFile => {
        if (logoFile.size/1024 > 50) {
            message.error("FIle size should be less than 50kb")
        } else {
            const org = {...this.state.org, logoFile}
            this.setState({org})
            message.success("File Verified")
        }
        return false
    }

    saveOrgDetails = async () => {
        if (this.validate()) {
            try {
                const formData = new FormData();
                formData.append("id", this.state.org.id)
                formData.append("name", this.state.org.name)
                formData.append("legalName", this.state.org.legalName)
                formData.append("logoFile", this.state.org.logoFile)
                const res = await OrgService.saveOrgDetails(formData)
                message.success("Org Details Saved")
                this.refresh()
            } catch (e) {
                message.error("Something Went Wrong")
                console.log(e)
            }
        }
    }

    handleNameChange = e => {
        const name = e.target.value
        const org = {...this.state.org, name}
        this.setState({org})
    }

    handleLegalNameChange = e => {
        const legalName = e.target.value
        const org = {...this.state.org, legalName}
        this.setState({org})
    }

    render () {

        const UploadButtonContent =
            this.state.org.id ?
                <img src={"/public/org/logo/" + this.state.org.id} alt={"current org logo"} />
                :
                <Button><UploadOutlined /></Button>

        return (
            <div>
                <h1 align={"center"} style={{margin: '40px'}}>Edit Org Details</h1>
                <Card bordered={false}>
                    <Row>
                        <Col offset={7} span={11}>
                            <Card type={"small"} bordered={false}>
                                <b>Enter Org Name: </b>
                                <Input
                                    value={this.state.org.name}
                                    onChange={this.handleNameChange}
                                    placeholder={"Org Name"}
                                />
                            </Card>
                        </Col>
                    </Row>
                    <Row>
                        <Col offset={7} span={11}>
                            <Card type={"small"} bordered={false}>
                                <b>Enter Legal Name: </b>
                                <Input
                                    value={this.state.org.legalName}
                                    onChange={this.handleLegalNameChange}
                                    placeholder={"Legal Name"}
                                />
                            </Card>
                        </Col>
                    </Row>
                    <Row>
                        <Col offset={10}>
                            <Card type={"small"} bordered={false}>
                                <Upload
                                    accept={'image/*'}
                                    showUploadList={false}
                                    beforeUpload={this.validateUpload}
                                >
                                    {UploadButtonContent}
                                </Upload>
                            </Card>
                        </Col>
                    </Row>
                    <Row>
                        <Col offset={11}>
                            <Card bordered={false} type={"small"}>
                                <Button type={"primary"} onClick={this.saveOrgDetails}>
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