import axios from "../axios";

const furl = "http://localhost:5000/";
class FaceMatchService {
    static Instance() {
        return new FaceMatchService()
    }

    lookup(image) {
        return axios.post(furl + 'lookup', {image:image});
    }

    register(image, userdata) {
        userdata.image = image;
        return axios.post(furl + 'register', userdata)
    }
}

export default FaceMatchService.Instance()