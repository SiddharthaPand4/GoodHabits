import React, { Component } from 'react';
import { Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink } from 'reactstrap';

export default class AppNavbar extends Component {
    constructor(props) {
        super(props);
        this.state = {isOpen: false};
        this.toggle = this.toggle.bind(this);
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    render() {
        return <Navbar color="light" expand="md">
            <NavbarBrand href="/"> <img src="logo.png" style={{width:200, marginTop: -2}} /></NavbarBrand>
            <NavbarToggler onClick={this.toggle}/>
            <Collapse isOpen={this.state.isOpen} navbar>
                <Nav className="ml-auto" navbar>
                    <NavItem>
                        <NavLink href="/#/raw">Raw</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink href="/#/summary">Summary</NavLink>
                    </NavItem>
                </Nav>
            </Collapse>
        </Navbar>;
    }
}