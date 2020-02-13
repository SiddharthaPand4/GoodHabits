import axios from "./axios";
import { authHeader } from '../helpers/auth-header';
import { config } from '../helpers/config'

class AnprReportService {

    static Instance() {
        return new AnprReportService()
    }

    getAnprEventsReport(req) {
        const request = {
            method: 'POST',
            headers: authHeader(),
            responseType: 'blob',
            data: JSON.stringify(req),
            url: config.apiUrl + 'api/anpr/anprevent'
        };
        return axios(request);
    }

}

export default AnprReportService.Instance()