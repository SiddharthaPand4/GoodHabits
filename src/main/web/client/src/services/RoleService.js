import axios from './axios';
import {authHeader} from "../helpers/auth-header";
import {config} from "../helpers/config";

class RoleService {

    static Instance() {
        return new RoleService()
    }
    getAllPrivilegeTypes(){
        return axios.get('/api/privilege/get/list')
    }

    addRole(role,flag)
    {
        const requestBody={
            id:role.id,
            name:role.name,
            privileges:role.privileges
        };
        const request = {
            method: 'POST',
            headers: authHeader(),
            data: JSON.stringify(requestBody),
            url: config.apiUrl + 'api/user/role'
        };
        if(flag){
            request.method='PUT';
        }
        return axios(request);
    }

    getRole(RoleId) {
        return axios.get('/api/user/role/' +RoleId);
    }

    removeRole(roleId){
        return axios.delete('/api/user/role/'+roleId);
    }

}

export default RoleService.Instance()