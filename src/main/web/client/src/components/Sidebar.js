import React, {Component} from 'react';
import {Icon, Layout, Menu} from "antd";
import {Link} from "react-router-dom";
import commonService from "../services/CommonService";
import UserService from "../services/UserService";

const {Sider} = Layout;
const {SubMenu} = Menu;

export default class Sidebar extends Component {

    constructor(props) {
        super(props);

        this.state = {
            collapsed: false,
            loaded: false,
            menu: {}
        };
    }

    componentDidMount() {
        UserService.getMenu().then(response => {
            let menu = response.data;
            menu.items = commonService.getSorted(menu.items, 'seq', true);
            menu.items.forEach(menuItem => {
                menuItem.submenu = commonService.getSorted(menuItem.submenu, 'seq', true);
            });

            this.setState({menu, loaded: true});
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
                    <img src={"park-n-secure-logo.jpg"} alt={"SynergyLabs Technology"}/>
                </div>
                <Menu theme="dark" mode="inline" defaultSelectedKeys={['0']}>
                    <Menu.Item key="0">
                        <Link to='/'><Icon type='home'/><span className='nav-text'>Home</span></Link>
                    </Menu.Item>

                    {(menu.items || []).map((item, index) =>

                        <SubMenu key={item.key} title={
                            <span><Icon type='box-plot'/><span>{item.title}</span></span>
                        }
                        >
                            {(item.submenu || []).map((subitem, index) =>
                                <Menu.Item key={subitem.key} className="sidebar-nav-link">
                                    <Link to={subitem.link}><span className='nav-text'>{subitem.title}</span></Link>
                                </Menu.Item>
                            )}
                        </SubMenu>
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