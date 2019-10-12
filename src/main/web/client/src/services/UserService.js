
import axios from "./axios";

class UserService {

    static Instance() {
        return new UserService()
    }

    getUser() {
        return axios.get('/api/user/');
    }

    getUsers() {
        return axios.get('/api/users');
    }

    isLoggedIn() {
        let token = JSON.parse(localStorage.getItem('syntoken'));
        if (!token) {
            console.log("cannot locate token, returning false");
            return false;
        }
        console.log("located token, returning true");
        return true
    }

    async login(username, password) {
        let response = await axios.post('/login', JSON.stringify({ username, password }), {headers: {
                'Content-Type': 'application/json',
            }});
        console.log("got this response", response);
        let token = response.data;
        if (token) {
            localStorage.setItem('syntoken', JSON.stringify(token));
        }
        return token;
    }

}

export default UserService.Instance()

//TODO valiate token,
// https://medium.com/@siddharthac6/json-web-token-jwt-the-right-way-of-implementing-with-node-js-65b8915d550e