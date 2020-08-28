import axios from './axios'

class OrgService {

    static Instance () {
        return new OrgService()
    }

    saveOrgDetails (org) {
        return axios.put('/api/org/details', org)
    }

    getOrgDetails () {
        return axios.get('/api/org/details')
    }

}

export default OrgService.Instance()