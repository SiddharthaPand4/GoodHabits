import React, {Component} from 'react';
import {Icon, Layout, Menu} from "antd";
import {Link} from "react-router-dom";
import UserService from "../services/UserService";

const {Sider} = Layout;
const {SubMenu} = Menu;

export default class Sidebar extends Component {
    state = {
        collapsed: false,
    };
    toggleCollapsed = () => {
        this.setState({
            collapsed: !this.state.collapsed,
        });
    };

    render() {
        return (
            <Sider
                collapsible
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
                    <img src={"orbitly-logo.png"}/>
                </div>
                <Menu theme="dark" mode="inline" defaultSelectedKeys={['0']}>
                    <Menu.Item key="0">
                        <Link to='/'><Icon type='home'/><span className='nav-text'>Home</span></Link>
                    </Menu.Item>


                    <SubMenu key="sub1" title={
                        <span>
                          <Icon type='box-plot'/>
                          <span>ANPR</span>
                        </span>
                    }
                    >
                        <Menu.Item key="1" className="sidebar-nav-link">
                            <Link to='/anpr'><span className='nav-text'>Events</span></Link>
                        </Menu.Item>
                        <Menu.Item key="2" className="sidebar-nav-link">
                            <Link to='/anpr/masterdata'><span className='nav-text'>Repeated Events</span></Link>
                        </Menu.Item>
                    </SubMenu>
                    {/*<SubMenu key="sub2" title={
                        <span>
                           <Icon type='box-plot'/>
                           <span>Offences</span>
                         </span>
                    }
                    >
                        <Menu.Item key="3" className="sidebar-nav-link">
                            <Link to='/incidents'><span className='nav-text'>Events</span></Link>
                        </Menu.Item>
                        <Menu.Item key="4" className="sidebar-nav-link">
                            <Link to='/incidents/repeated'><span className='nav-text'>Repeated Events</span></Link>
                        </Menu.Item>
                    </SubMenu>*/}

                    <Menu.Item key="5" className="sidebar-nav-link">
                        <Link to="/incidents/hotlisted"><Icon type='alert'/><span className='nav-text'>Hotlist</span></Link>
                    </Menu.Item>
                    {/*<Menu.Item key="6">
                        <Link to='/feed'><Icon type='video-camera'/><span className='nav-text'>Feed</span></Link>
                    </Menu.Item>*/}
                    {/*<SubMenu key="admin" title={<span><Icon type="deployment-unit"/>Admin</span>}>
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
                    </SubMenu>*/}
                    <SubMenu key="pgs" title={
                        <span>
                           <Icon type='car'/>
                           <span>Parking</span>
                         </span>
                    }>
                        {/*<Menu.Item key="pgs-1" className="sidebar-nav-link">
                            <Link to='/pgs/dashboard'><span className='nav-text'>Dashboard</span></Link>
                        </Menu.Item>*/}
                        <Menu.Item key="pgs-2" className="sidebar-nav-link">
                            <Link to='/pgs/console'><span className='nav-text'>Operator Console</span></Link>
                        </Menu.Item>
                        <Menu.Item key="pgs-3" className="sidebar-nav-link">
                            <Link to='/pgs/inout'><span className='nav-text'>Check-In/Out</span></Link>
                        </Menu.Item>
                        <Menu.Item key="pgs-4" className="sidebar-nav-link">
                            <Link to='/pgs/reports'><span className='nav-text'>Reports</span></Link>
                        </Menu.Item>
                    </SubMenu>

                    {/*<SubMenu
                        key="sub3"
                        title={
                            <span>
                         <Icon type='box-plot'/>
                         <span>People Counting</span>
                       </span>
                        }
                    >
                        <Menu.Item key="7" className="sidebar-nav-link">
                            <Link to='/people-counting/events'><span className='nav-text'>Events</span></Link>
                        </Menu.Item>
                    </SubMenu>*/}

                     <SubMenu
                                            key="sub3"
                                            title={
                                                <span>
                                             <Icon type='box-plot'/>
                                             <span>People Counting</span>
                                           </span>
                                            }
                                        >
                                            <Menu.Item key="7" className="sidebar-nav-link">
                                                <Link to='/people-counting/events'><span className='nav-text'>Events</span></Link>
                                            </Menu.Item>
                                            <Menu.Item key="8" className="sidebar-nav-link"> <Link to='/people-counting/dashboard'><span className='nav-text'>Dashboard</span></Link>
                                                                                                                                                 </Menu.Item>
                                        </SubMenu>

                    <Menu.Item key="7">
                        <Link to='/' onClick={() => UserService.logout()}><Icon type='logout'/><span
                            className='nav-text'>Logout</span></Link>
                    </Menu.Item>

                </Menu>
            </Sider>
        );
    }
}