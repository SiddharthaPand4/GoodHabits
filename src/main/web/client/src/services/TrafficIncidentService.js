
import axios from "./axios";

class TrafficIncidentService {

    static Instance() {
        return new TrafficIncidentService()
    }

    getIncidents(filter) {
        return axios.post('/api/traffic/incident/', filter);
    }

    archiveIncident(incident) {
        return axios.delete('/api/traffic/incident/' + incident.id);
    }
}

export default TrafficIncidentService.Instance()