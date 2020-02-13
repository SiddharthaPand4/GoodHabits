import axios from "./axios";

class VidsService {

    static Instance() {
        return new VidsService()
    }

    getIncidents(filter) {
        return axios.post('/api/vids/incidents', filter);
    }

    archiveIncident(event) {
        return axios.delete('/api/vids/' + event.id);
    }

    updateIncident(event) {
        return axios.put('/api/vids/event', event);
    }

    getIncidentImage(id) {
        return axios.get('/public/vids/image/' + id + '/image.jpg');
    }

    getIncidentVideo(id) {
        return axios.get('/public/vids/video/' + id + '/video.mp4');
    }

}

export default VidsService.Instance()