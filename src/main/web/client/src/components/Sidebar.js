import React, {Component} from 'react';
import {Icon, Layout, Menu} from "antd";
import {Link} from "react-router-dom";
import UserService from "../services/UserService";

const {Sider} = Layout;
const {SubMenu} = Menu;

export default class Sidebar extends Component {
    state = {
        collapsed: false,
        loaded: false,
        menu: {}
    };

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        UserService.getMenu().then(response => {
            this.setState({menu: response.data, loaded: true});
        });
    }

    toggleCollapsed() {
        this.setState({
            collapsed: !this.state.collapsed,
        });
    };

    render() {

        let menu = this.state.menu;
        console.log("admin?", menu.admin)
        return (
            <Sider
                className={"no-print"}
                collapsible
                breakpoint="lg"
                collapsedWidth="0"
            >
                <div className="logo">
                    <img src={"synlabs-logo.png"}/>
                </div>
                <Menu theme="dark" mode="inline" defaultSelectedKeys={['0']}>
                    <Menu.Item key="0">
                        <Link to='/'><Icon type='home'/><span className='nav-text'>Home</span></Link>
                    </Menu.Item>

                    <SubMenu
                        key="frs"
                        title={
                            <span>
                                             <Icon type='box-plot'/>
                                             <span>FRS</span>
                                           </span>
                        }
                    >
                        <Menu.Item key="frs-1">
                            <Link to='/register'><Icon type='video-camera'/><span
                                className='nav-text'>Register</span></Link>
                        </Menu.Item>
                        <Menu.Item key="frs-2">
                            <Link to='/frsuser'><Icon type='video-camera'/><span
                                className='nav-text'>Registered Users</span></Link>
                        </Menu.Item>
                        <Menu.Item key="frs-3">
                            <Link to='/frsevents'><Icon type='video-camera'/><span
                                className='nav-text'>Events</span></Link>
                        </Menu.Item>
                        <Menu.Item key="frs-4" className="sidebar-nav-link">
                            <Link to='/feed'> <Icon type='box-plot'/>
                                <span className='nav-text'>Feed</span></Link>
                        </Menu.Item>
                        <Menu.Item key="frs-5" className="sidebar-nav-link">
                            <Link to='/userguide'> <Icon type='box-plot'/>
                                <span className='nav-text'>User Guide</span></Link>
                        </Menu.Item>
                    </SubMenu>
                    {menu.admin &&
                    <SubMenu key="admin" title={<span><Icon type="deployment-unit"/>Admin</span>}>
                        <Menu.Item key="51">
                            <Link to='/user'><Icon type='user'/><span className='nav-text'>Users</span></Link>
                        </Menu.Item>
                    </SubMenu>
                    }
                    <Menu.Item key="logout">
                        <Link to='/' onClick={() => UserService.logout()}><Icon type='logout'/><span
                            className='nav-text'>Logout</span></Link>
                    </Menu.Item>
                </Menu>
            </Sider>
        );
    }
}