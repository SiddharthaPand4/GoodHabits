import React, {Component} from 'react';

import './App.css';
import {Layout} from 'antd';
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
import FaceMatchView from "./components/facerec/FaceMatchView";

import ConfigView from "./views/Polygon/ConfigView";
import RegisteredView from "./components/facerec/RegisteredView";


const {Content} = Layout;

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {loggedIn: false};

        EventBus.subscribe('login-logout', (event) => this.refreshMenu(event))
    }

    componentDidMount() {
        this.refreshMenu()
    }

    refreshMenu() {
        this.setState({loggedIn: UserService.isLoggedIn()});
    }

    render() {

        const isLoggedIn = this.state.loggedIn;

        const sideBar = isLoggedIn ? <Sidebar/> : null;
        const header = isLoggedIn ? <Headbar isLoggedIn={isLoggedIn}/> : null;


        return (
            <div className="App">
                <Layout style={{minHeight: '100vh'}}>
                    {sideBar}
                    <Layout>
                        <Content style={{margin: '16px'}}>
                            <div style={{padding: 4}}>
                                <Route path='/login' exact={true} component={LoginView}/>
                                <PrivateRoute path='/' exact={true} component={HomeView}/>
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
                                <PrivateRoute path='/register' exact={true} component={FaceMatchView}/>
                                <PrivateRoute path='/frsuser' exact={true} component={RegisteredView}/>
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