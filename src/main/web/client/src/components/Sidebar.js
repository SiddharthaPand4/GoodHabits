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
                        item.submenu != null ?
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
                    <Menu.Item key="7">
                        <Link to='/' onClick={() => UserService.logout()}><Icon type='logout'/><span
                            className='nav-text'>Logout</span></Link>
                    </Menu.Item>


                </Menu>
            </Sider>
        );
    }
}