import axios from "./axios";

class AnprService {

    static Instance() {
        return new AnprService()
    }

    getEvents(filter) {
        return axios.post('/api/anpr/events', filter);
    }

    getIncidents(filter) {
        return axios.post('/api/anpr/incidents', filter);
    }

    archiveEvent(event) {
        return axios.delete('/api/anpr/' + event.id);
    }

    getEventFile(id) {
        return axios.get('/public/anpr/lpr/' + id + '/image.jpg');
    }
}

export default AnprService.Instance()