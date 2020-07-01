import axios from "../axios";

class FaceMatchService {
    static Instance() {
        return new FaceMatchService()
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
}

export default FaceMatchService.Instance()