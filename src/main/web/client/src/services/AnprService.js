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

    getIncidentsHotlisted(filter) {
        return axios.post('/api/anpr/incidents/hotListed', filter);
    }

    archiveEvent(event) {
        return axios.delete('/api/anpr/' + event.id);
    }

    archiveAllEvent(lpr) {
            return axios.put('/api/anpr/events/archive/' + lpr);
        }

    updateEvent(event) {
        return axios.put('/api/anpr/event', event);
    }

    getEventFile(id) {
        return axios.get('/public/anpr/anprText/' + id + '/image.jpg');
    }

    getHelmetMissingIncidentsRepeated(filter) {
            return axios.post('/api/incident/repeated/helmet-missing', filter);
    }
    getReverseDirectionIncidentsRepeated(filter) {
            return axios.post('/api/incident/repeated/reverse', filter);
    }
    getBriefIncidentsRepeated(filter) {
                return axios.post('/api/incident/timeline', filter);
    }
     getIncidentTimeline(filter) {
                     return axios.post('/api/anpr/events/list/bylpr', filter);
     }
     getIncidentsList(filter) {
                    return axios.post('/api/anpr/events/list/lpr/count', filter);
     }

}

export default AnprService.Instance()