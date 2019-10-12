
import axios from 'axios';

class AnprService {

    static Instance() {
        return new AnprService()
    }

    getEvents() {
        return axios.get('/api/anpr/events');
    }

}

export default AnprService.Instance()