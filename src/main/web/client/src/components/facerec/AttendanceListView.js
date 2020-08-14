import React, {Component} from "react";


export default class AttendanceListView extends Component {

    constructor(props) {
        super(props);

        this.refresh = this.refresh.bind(this);
    }

    componentDidMount() {
        refresh()
    }

    refresh() {

    }

    render() {
        return (<div>list</div>)
    }
}