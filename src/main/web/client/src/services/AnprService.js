
import axios from "./axios";

class AnprService {

    static Instance() {
        return new AnprService()
    }

    getEvents(filter) {
        return axios.post('/api/anpr/events',filter);
    }

     archiveEvent(event) {
         return axios.delete('/api/anpr/' + event.id);
     }
}

export default AnprService.Instance()