
import React, {Component} from "react";
import {Button,  Card, DatePicker, Icon, TimePicker} from 'antd';
import DeviceService from "../services/DeviceService";
import {Link} from "react-router-dom";
const ButtonGroup = Button.Group;

export default class DeviceView extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            layout: "table",
            filter: {
                page: 1,
                pageSize: 10
            }
        };
        this.onFromDateChange = this.onFromDateChange.bind(this);
        this.onFromTimeChange = this.onFromTimeChange.bind(this);
        this.onToDateChange = this.onToDateChange.bind(this);
        this.onToTimeChange = this.onToTimeChange.bind(this);
        this.onPageChange = this.onPageChange.bind(this);
        this.onPageSizeChange = this.onPageSizeChange.bind(this);
        this.refresh = this.refresh.bind(this);
    }

    refresh(){
        this.props.handleRefresh();
    }
    onPageChange(page, pageSize) {
        let filter = this.state.filter;
        filter.page = page;
        filter.pageSize = pageSize;
        this.refreshNow(filter)
    }

    onPageSizeChange(current, pageSize) {
        let filter = this.state.filter;
        filter.pageSize = pageSize;
        this.refreshNow(filter);
    }

    changeLayout(layout) {
        this.setState({"layout": layout});
        this.props.handleLayoutChange(layout);
    }

   onFromDateChange(date) {
       let filter = this.state.filter;
       if(date!=null){
         filter.fromDate = date.format("YYYY-MM-DD");
       }
       else{
           filter.fromDate=null;
       }
       this.setState({filter: filter});
       this.props.handleFilterChange(filter);
   }

   onFromTimeChange(time) {
       let filter = this.state.filter;
       if(time!=null){
         filter.fromTime = time.format("HH:mm:ss");
       }
       else{
           filter.fromTime=null;
       }
       this.setState({filter: filter});
       this.props.handleFilterChange(filter);
   }

   onToDateChange(date) {
       let filter = this.state.filter;
       if(date!=null){
         filter.toDate = date.format("YYYY-MM-DD");
       }
       else{
         filter.toDate=null;
       }
       this.setState({filter: filter});
       this.props.handleFilterChange(filter);
   }

   onToTimeChange(time) {
       let filter = this.state.filter;
       if(time!=null){
        filter.toTime = time.format("HH:mm:ss");
       }
       else{
         filter.toTime=null;
       }
       this.setState({filter: filter});
       this.props.handleFilterChange(filter);
   }

    render() {

        let layout = this.state.layout;

        return (
            <div>
                <span>From: </span>
                <DatePicker onChange={this.onFromDateChange}/>&nbsp;&nbsp;
                <TimePicker minuteStep={15} secondStep={60} onChange={this.onFromTimeChange}/>&nbsp;&nbsp;
                <span>To: </span>
                <DatePicker onChange={this.onToDateChange}/>&nbsp;&nbsp;
                <TimePicker minuteStep={15} secondStep={60} onChange={this.onToTimeChange}/>&nbsp;&nbsp;

                <ButtonGroup>
                    <Button type={layout === "list" ? "primary" : ""} size="small" icon="unordered-list"
                            onClick={() => {
                                this.changeLayout("list")
                            }}/>
                    <Button type={layout === "table" ? "primary" : ""} size="small" icon="table"
                            onClick={() => {
                                this.changeLayout("table")
                            }}/>
                </ButtonGroup>&nbsp;&nbsp;
                <Button onClick={() => {
                    this.refresh()
                }}><Icon type="reload"/>Reload</Button>
            </div>
        )
    }
}
