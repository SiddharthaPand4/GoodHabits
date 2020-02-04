import axios from "./axios";
import moment from 'moment';
import { authHeader } from '../helpers/auth-header';
import { config } from '../helpers/config'

class ReportService {

    static Instance() {
        return new ReportService()
    }

    getParkingEventsReport(req) {

         const request = {
                method: 'POST',
                headers: authHeader(),
                responseType: 'blob',
                data: JSON.stringify(req),
                url: config.apiUrl + 'api/report/parkingevents'
            };
        return axios(request);
    }

}

export default ReportService.Instance()