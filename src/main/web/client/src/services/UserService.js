import axios from "./axios";
import { authHeader } from '../helpers/auth-header';
import { config } from '../helpers/config'

class UserService {

    static Instance() {
        return new UserService()
    }

    getUser(userId) {
        return axios.get('/api/user/' + userId);
    }

    getUsers() {
        return axios.get('/api/user/');
    }

    getRoles(){
        return axios.get('/api/user/roles');
    }

     createUser(user){
        const requestBody = {
             userName:user.userName,
             lastName:user.lastName,
             firstName:user.firstName,
             email:user.email,
             id:user.id,
             roles:user.roles,
        };

        const request = {
            method: 'POST',
            headers: authHeader(),
            data: JSON.stringify(requestBody),
            url: config.apiUrl + 'api/user/'
        };
        if(user.id){
            request.method='PUT';
        }
        return axios(request);
    }

    deleteUser(userId){
        return axios.delete('/api/user/' + userId);
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