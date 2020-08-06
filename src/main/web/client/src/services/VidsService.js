import axios from "./axios";
import {authHeader} from '../helpers/auth-header';
import {config} from '../helpers/config'

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

    getStats() {
        return axios.get('/api/vids/stats');
    }

    downloadVideo(id){
        const request = {
            method: 'GET',
            headers: authHeader(),
            responseType: 'blob',
            timeout: 30000,
            url: config.apiUrl + 'public/vids/video/' + id + "/video.mp4"
        };
        return axios(request);
    }

    downloadScreenshot(id){
        const request = {
            method: 'GET',
            headers: authHeader(),
            responseType: 'blob',
            timeout: 30000,
            url: config.apiUrl + 'public/vids/image/' + id + "/image.jpg"
        };
        return axios(request);
    }

}

export default VidsService.Instance()