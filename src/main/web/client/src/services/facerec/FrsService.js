import axios from "../axios";

class FrsService {
    static Instance() {
        return new FrsService()
    }

    lookup(image) {
        return axios.post('/api/frs/lookup', {image:image});
    }

    register(userdata, image) {
        userdata.image = image;
        return axios.post('/api/frs/register', userdata)
    }

    getRegisteredUsers(filter) {
        return axios.post('/api/frs/users', filter);
    }

    getEvents(filter) {
        return axios.post('/api/frs/events', filter);
    }

    toggleWhiteList(uid) {
        return axios.post('/api/frs/toggle/' + uid)
    }
}

export default FrsService.Instance()