import React, {Component} from 'react';
import {Icon, Layout, Menu,} from "antd";
import {Link} from "react-router-dom";
import UserService from "../services/UserService";

const {Sider} = Layout;
const {SubMenu} = Menu;

export default class Sidebar extends Component {
    render() {
        return (
            <Sider
                breakpoint="lg"
                collapsedWidth="0"
                onBreakpoint={broken => {
                    console.log(broken);
                }}
                onCollapse={(collapsed, type) => {
                    console.log(collapsed, type);
                }}
            >
                <div className="logo">
                    <img src={"synlabs-logo.png"}/>
                </div>
                <Menu theme="dark" mode="inline" defaultSelectedKeys={['0']}>
                    <Menu.Item key="0">
                        <Link to='/'><Icon type='home'/><span className='nav-text'>Home</span></Link>
                    </Menu.Item>
                    <Menu.Item key="1" className="sidebar-nav-link">
                        <Link to='/incidents'><Icon type='alert'/><span className='nav-text'>Incidents</span></Link>
                    </Menu.Item>
                    <Menu.Item key="3" className="sidebar-nav-link">
                        <Link to='/anpr'><Icon type='box-plot'/><span className='nav-text'>ANPR</span></Link>
                    </Menu.Item>
                    <Menu.Item key="4">
                        <Link to='/feed'><Icon type='video-camera'/><span className='nav-text'>Feed</span></Link>
                    </Menu.Item>
                    <SubMenu key="admin" title={<span><Icon type="deployment-unit"/>Admin</span>}>
                        <Menu.Item key="51">
                            <Link to='/user'><Icon type='user'/><span className='nav-text'>Users</span></Link>
                        </Menu.Item>
                        <Menu.Item key="52">
                            <Link to='/device'><Icon type='laptop'/><span className='nav-text'>Device</span></Link>
                        </Menu.Item>
                        <Menu.Item key="53">
                            <Link to='/trigger'><Icon type="clock-circle"/><span
                                className='nav-text'>Triggers</span></Link>
                        </Menu.Item>
                    </SubMenu>
                    <Menu.Item key="6">
                        <Link to='/' onClick={() => UserService.logout()}><Icon type='logout'/><span
                            className='nav-text'>Logout</span></Link>
                    </Menu.Item>
                </Menu>
            </Sider>
        );
    }
}