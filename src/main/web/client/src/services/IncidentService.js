
import axios from "./axios";

class IncidentService {

    static Instance() {
        return new IncidentService()
    }

    getIncidents(filter) {
        return axios.post('/api/incidents', filter);
    }

    archiveIncident(incident) {
        return axios.delete('/api/incident/' + incident.ID);
    }
}

export default IncidentService.Instance()