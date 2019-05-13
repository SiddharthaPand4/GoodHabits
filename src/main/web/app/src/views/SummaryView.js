import React, { Component } from 'react';
import '../App.css';
import AppNavbar from '../components/AppNavBar';
import { Container } from 'reactstrap';
import RawDataList from "../components/RawData";
import SummaryDataList from "../components/SummaryData";


class Home extends Component {

    componentDidMount() {
        document.title = "Atcc";
    }

    render() {
        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <div>Summary</div>
                    <SummaryDataList/>
                </Container>
            </div>
        );
    }
}

export default Home;