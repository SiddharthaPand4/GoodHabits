import React, {Component} from 'react';

import './App.css';
import {Layout, notification, Modal, Tag, Icon, Card, Typography} from 'antd';
import Sidebar from "./components/Sidebar";
import Headbar from "./components/Headbar";
import Footerbar from "./components/Footerbar";
import PrivateRoute from "./components/PrivateRoute";
import HomeView from "./views/HomeView";
import FeedView from "./views/FeedView";
import {Route} from "react-router-dom";
import DeviceView from "./views/DeviceView";
import UserListView from "./views/UserListView";
import DeviceConfigView from "./views/DeviceConfigView";

import UserService from "./services/UserService";
import {EventBus} from "./components/event"
import LoginView from "./views/LoginView";
import TriggerView from "./views/TriggerView";
import AnprView from "./views/anpr/AnprView";
import TrafficIncidentView from "./views/TrafficIncidentView";
import IncidentRepeatedView from "./views/incidents/IncidentRepeatedView";
import IncidentHotlistView from "./views/IncidentHotlistView";
import ParkingDashboardView from "./views/parking/ParkingDashboardView";
import ParkingConsoleView from "./views/parking/ParkingConsoleView";
import PgsReportView from "./views/parking/ParkingReportView";
import ParkingInOutView from "./views/parking/ParkingInOutView";
import PeopleCounting from "./views/PeopleCount/PeopleCounting";
import ApcDashboard from "./views/PeopleCount/ApcDashboard";
import AtccView from "./views/atcc/AtccView";
import AtccSummaryView from "./views/atcc/AtccSummaryView";
import HighwayIncidentView from "./views/vids/HighwayIncidentView";
import HighwayIncidentDashboardView from "./views/vids/HighwayIncidentDashboardView";
import AnprReportView from "./views/anpr/AnprReportView";
import FaceRegisterView from "./components/facerec/FaceRegisterView";

import ConfigView from "./views/Polygon/ConfigView";
import RegisteredView from "./components/facerec/RegisteredView";
import FrsEventView from "./components/facerec/FrsEventView";
const {Text} = Typography;
const {Content} = Layout;

var stompClient = null;

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loggedIn: false,
            channelConnected: false,
            showAlert :false,
            alert:{}
        };

        this.onConnected = this.onConnected.bind(this);
        this.onMessageReceived = this.onMessageReceived.bind(this);
        EventBus.subscribe('login-logout', (event) => this.refreshMenu(event))
    }

    componentDidMount() {
        this.refreshMenu()
        this.connect();
    }

    componentWillUnmount() {
        this.disconnect();
    }

    connect() {
        const Stomp = require('stompjs')

        var SockJS = require('sockjs-client')

        SockJS = new SockJS('/ws')

        stompClient = Stomp.over(SockJS);

        stompClient.connect({}, this.onConnected, this.onError);
    }

    onConnected() {
        console.log("connected!")
        this.setState({
            channelConnected: true
        })
        stompClient.subscribe('/alert', this.onMessageReceived);
    }

    onError() {
        console.log("error connecting!")
    }

    onMessageReceived(payload) {
        const isLoggedIn = this.state.loggedIn;

        if (!isLoggedIn) return;

        let alert = JSON.parse(payload.body);
        console.log("rcvd alert", alert);
        const args = {
            message: alert.message,
            description: <Card
                style={{margin: "5px"}}
                title={
                    <div>
                        {(alert.person) ? <Tag color="#f50">{alert.person.type}</Tag> : ""}
                        <Text
                            type="secondary">{(alert.person) ? "ID: " + alert.person.pid : ""}</Text>
                        <Text
                            type="secondary">{(alert.person) ? "  Name: " + alert.person.name : ""}</Text>

                    </div>
                }
                bordered={true}
            >
                <div style={{textAlign: "center"}}>
                    <img alt="face" style={{width:100,height:100, borderRadius:"50%"}}
                         src={"/public/frs/event/face/" + alert.uid + "/image.jpg"}/>
                </div>
                <div style={{marginTop: "5px", textAlign: "center"}}>
                    <div style={{textAlign: "center"}}>
                        <img alt="person" style={{width:200,height:200}}
                             src={"/public/frs/event/full/" + alert.uid + "/image.jpg"}/>
                    </div>
                </div>

            </Card>,
            duration: 0,
        };
        notification.open(args);
    }

    refreshMenu() {
        this.setState({loggedIn: UserService.isLoggedIn()});
    }

    render() {

        const isLoggedIn = this.state.loggedIn;
        const showAlert = this.state.showAlert;
        const alert = this.state.alert;
        const sideBar = isLoggedIn ? <Sidebar/> : null;
        const header = isLoggedIn && showAlert ? <Headbar alert={alert} isLoggedIn={isLoggedIn}/> : null;


        return (
            <div className="App">

                <Layout style={{minHeight: '100vh'}}>

                    {sideBar}

                    <Layout>
                        {header}
                        <Content style={{margin: '16px'}}>
                            <div style={{padding: 4}}>
                                <Route path='/login' exact={true} component={LoginView}/>
                                <PrivateRoute path='/' exact={true} component={FrsEventView}/>
                                <PrivateRoute path='/incidents' exact={true} component={TrafficIncidentView}/>
                                <PrivateRoute path='/incidents/hotlisted' exact={true} component={IncidentHotlistView}/>
                                <PrivateRoute path='/incidents/repeated' exact={true} component={IncidentRepeatedView}/>
                                <PrivateRoute path='/anpr/report' exact={true} component={AnprReportView}/>
                                <PrivateRoute path='/anpr' exact={true} component={AnprView}/>
                                <PrivateRoute path='/user' exact={true} component={UserListView}/>
                                <PrivateRoute path='/feed' exact={true} component={FeedView}/>
                                <PrivateRoute path='/trigger' exact={true} component={TriggerView}/>
                                <PrivateRoute path='/device' exact={true} component={DeviceView}/>
                                <PrivateRoute path='/device/conf' exact={true} component={DeviceConfigView}/>
                                <PrivateRoute path='/people-counting/events' exact={true} component={PeopleCounting}/>


                                {/* PGS is parking guideance and management system */}
                                <PrivateRoute path='/pgs/dashboard' exact={true} component={ParkingDashboardView}/>
                                <PrivateRoute path='/pgs/console' exact={true} component={ParkingConsoleView}/>
                                <PrivateRoute path='/pgs/inout' exact={true} component={ParkingInOutView}/>
                                <PrivateRoute path='/pgs/reports' exact={true} component={PgsReportView}/>
                                <PrivateRoute path='/people-counting/Dashboard' exact={true} component={ApcDashboard}/>

                                {/* atcc */}
                                <PrivateRoute path='/atcc' exact={true} component={AtccView}/>
                                <PrivateRoute path='/atcc/summary' exact={true} component={AtccSummaryView}/>

                                {/* vids */}
                                <PrivateRoute path='/vids' exact={true} component={HighwayIncidentView}/>
                                <PrivateRoute path='/vids/dashboard' exact={true} component={HighwayIncidentDashboardView}/>

                                <PrivateRoute path='/feedStream' exact={true} component={ConfigView}/>

                                {/* face rec */}
                                <PrivateRoute path='/register' exact={true} component={FaceRegisterView}/>
                                <PrivateRoute path='/frsuser' exact={true} component={RegisteredView}/>
                                <PrivateRoute path='/frsevents' exact={true} component={FrsEventView}/>

                            </div>
                        </Content>
                        <Footerbar/>
                    </Layout>
                </Layout>

            </div>
        );

    }
}

export default App;