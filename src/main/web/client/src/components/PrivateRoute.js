import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import UserService from "../services/UserService";

const PrivateRoute = ({ component: Component, ...rest }) => (
    <Route {...rest} render={props => (
        UserService.isLoggedIn()
            ? <Component {...props} />
            : <Redirect to={{ pathname: '/login', state: { from: props.location } }} />
    )} />
);

export default PrivateRoute