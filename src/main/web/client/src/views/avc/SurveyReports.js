import React, {Component} from 'react'
import AvcService from "../../services/AvcService"
import {Table} from "antd"
import {FileExcelOutlined} from '@ant-design/icons'

const {Column} = Table

export default class SurveyReports extends Component {
    state = {
        surveys: []
    }

    generateReport = e => {
        console.log(e)
    }

    componentDidMount() {
        this.fetchSurveys()
    }

    fetchSurveys = async () => {
        const res = await AvcService.fetchSurveys()
        const surveys = res.data
        this.setState({surveys})
    }

    render() {
        const data = this.state.surveys.map( (survey, i) => {
            return {...survey, key: i+1, report: i}
        })
        return (
            <div>
                <h1 align={"center"} style={{margin: '40px'}}>Survey Reports</h1>
                <Table dataSource={data}>
                    <Column title={"Name"} dataIndex={"name"} key={"name"} />
                    <Column title={"Start Date"} dataIndex={"startDate"} key={"startDate"} />
                    <Column title={"End Date"} dataIndex={"endDate"} key={"endDate"} />
                    <Column
                        title={"Report"}
                        dataIndex={"report"}
                        key={"report"}
                        render = {()=> (
                            <div onClick={this.generateReport}><FileExcelOutlined /></div>
                        )}
                    />
                </Table>
            </div>
        )
    }

}