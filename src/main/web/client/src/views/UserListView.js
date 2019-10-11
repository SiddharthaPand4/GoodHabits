import React, {Component} from "react";
import {Table, Divider} from 'antd';
import UserService from "../services/UserService";
const { Column} = Table;
export default class UserListView extends Component {

    constructor(props) {
        super(props);
        this.state = {loading: true, users: []}
    }

    componentDidMount() {
        UserService.getUsers().then(request => {
            this.setState({"users": request.data, loading: false})
        })
    }

    render() {
        return (
            <Table dataSource={this.state.users} >
                <Column title="Username" dataIndex="username" key="username" render={(text, record) => (
                    <span><a href={"user/" + record.ID}>{text}</a></span>
                )}/>
                <Column title="Email" dataIndex="email" key="email" />
                <Column title="Token" dataIndex="token" key="token" />
                <Column title="Action" key="action" render={(text, record) => (
                        <span>
                            <a>Edit {record.lastName}</a>
                            <Divider type="vertical" />
                            <a>Delete</a>
                        </span>
                    )}
                />
            </Table>
        )
    }
}