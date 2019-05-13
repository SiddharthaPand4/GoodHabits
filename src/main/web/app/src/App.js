import React, { Component } from 'react';
import './App.css';
import { HashRouter as Router, Route, Switch } from 'react-router-dom';
import Home from "./views/Home";
import SummaryView from "./views/SummaryView";

class App extends Component {

    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={Home}/>
                    <Route path='/raw' exact={true} component={Home}/>
                    <Route path='/summary' exact={true} component={SummaryView}/>
                </Switch>
            </Router>
        )
    }
}

export default App;
