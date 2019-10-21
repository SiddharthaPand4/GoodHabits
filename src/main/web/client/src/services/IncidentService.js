
import axios from "./axios";

class IncidentService {

    static Instance() {
        return new IncidentService()
    }

    getIncidents(filter) {
        return axios.post('/api/incident', filter);
    }

    archiveIncident(incident) {
        return axios.delete('/api/incident/' + incident.id);
    }
}

export default IncidentService.Instance()