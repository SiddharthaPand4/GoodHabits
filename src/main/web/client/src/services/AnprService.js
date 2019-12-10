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

    updateEvent(event) {
        return axios.put('/api/anpr/event', event);
    }

    getEventFile(id) {
        return axios.get('/public/anpr/anprText/' + id + '/image.jpg');
    }
}

export default AnprService.Instance()