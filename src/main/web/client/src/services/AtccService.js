import axios from "./axios";
import {authHeader} from '../helpers/auth-header';
import {config} from '../helpers/config'

class AtccService {

    static Instance() {
        return new AtccService()
    }

    getAtccData(filter) {
        return axios.put("/api/atcc/raw", filter);
    }

    getAtccSummaryData(filter, interval) {
        return axios.put("/api/atcc/summary?interval=" + interval, filter);
    }

    getEvents(filter) {
        return axios.post('/api/atcc/events', filter);
    }

    getAtccReport(req) {
        let path = 'api/report/vids/incidents/all';

        switch (req.reportType) {
            case "All Incidents":
                path = 'api/report/vids/incidents/all';
                break;
            case "DayWise Incidents Summary":
                path = 'api/report/vids/incidents/summary/day-wise';
                break;
            case "All Vehicles Traffic Events":
                path = 'api/report/atcc/events/all';
                break;
            case "Vehicles Traffic Events Summary":
                path = 'api/report/atcc/events/summary/day-wise';
                break;

        }
        const request = {
            method: 'POST',
            headers: authHeader(),
            responseType: 'blob',
            timeout: 30000,
            data: JSON.stringify(req),
            url: config.apiUrl + path
        };
        return axios(request);
    }

    downloadVideo(id){
        const request = {
            method: 'GET',
            headers: authHeader(),
            responseType: 'blob',
            timeout: 30000,
            url: config.apiUrl + 'public/atcc/video/' + id
        };
        return axios(request);
    }


}

export default AtccService.Instance()