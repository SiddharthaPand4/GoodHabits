import React, {Component} from 'react';
import {Icon, Layout, Menu} from "antd";
import {Link} from "react-router-dom";
import UserService from "../services/UserService";
import UserSwitchOutlined from "@ant-design/icons/lib/icons/UserSwitchOutlined";

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

                    {(menu.items || []).map((item, index) =>
                    item.submenu !=null ?
                        (<SubMenu key={item.key} title={
                            <span><Icon type={item.icon}/><span>{item.title}</span></span>
                        }
                        >
                            {(item.submenu || []).map((subitem, index) =>
                                    <Menu.Item key={subitem.key} className="sidebar-nav-link">
                                        <Link to={subitem.link}><span className='nav-text'>{subitem.title}</span></Link>
                                    </Menu.Item>
                                )}
                        </SubMenu>)
                        :
                        <Menu.Item key={item.key}>
                            <Link to={item.link}><Icon type={item.icon}/><span
                                className='nav-text'>{item.title}</span></Link>
                        </Menu.Item>
                    )}



                    <Menu.Item key="6" className="sidebar-nav-link">
                    <Link to='/feed'>  <Icon type='box-plot'/>
                    <span  className='nav-text'>
                           <span>Feed</span>
                        </span></Link>
                    </Menu.Item>


                    <SubMenu key="admin" title={<span><Icon type="deployment-unit"/>Admin</span>}>
                        <Menu.Item key="51">
                            <Link to='/user'><Icon type='user'/><span className='nav-text'>Users</span></Link>
                        </Menu.Item>
                        <Menu.Item key="52">
                            <Link to='/roles'><Icon type="team" /><span className='nav-text'>Roles</span></Link>
                        </Menu.Item>
                        <Menu.Item key="53">
                            <Link to='/device'><Icon type='laptop'/><span className='nav-text'>Device</span></Link>
                        </Menu.Item>
                        <Menu.Item key="54">
                            <Link to='/trigger'><Icon type="clock-circle"/><span
                                className='nav-text'>Triggers</span></Link>
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
                        <Menu.Item key="9" className="sidebar-nav-link">
                            <Link to='/people-counting/events'><span className='nav-text'>Events</span></Link>
                        </Menu.Item>
                        <Menu.Item key="8" className="sidebar-nav-link"> <Link to='/people-counting/dashboard'><span
                            className='nav-text'>Dashboard</span></Link>
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