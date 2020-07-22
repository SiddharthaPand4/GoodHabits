import axios from "./axios";
import { authHeader } from '../helpers/auth-header';
import { config } from '../helpers/config'

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

        const request = {
            method: 'POST',
            headers: authHeader(),
            responseType: 'blob',
            data: JSON.stringify(req),
            url: config.apiUrl + 'api/report/atcc/events'
        };
        return axios(request);
    }


}

export default AtccService.Instance()