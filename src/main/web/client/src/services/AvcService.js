import axios from "./axios"
import {authHeader} from "../helpers/auth-header";
import {config} from "../helpers/config";

class AvcService {

    static Instance() {
        return new AvcService()
    }

    saveSurvey(survey) {
        return axios.post('/api/survey/', survey)
    }

    fetchSurveys() {
        return axios.get('/api/survey/list')
    }

    generateReport(surveyId) {

        const request = {
            method: 'GET',
            headers: authHeader(),
            responseType: 'blob',
            timeout:180000,
            url: config.apiUrl + 'api/report/avc/survey?surveyId=' + surveyId
        };
        return axios(request);
    }

}

export default AvcService.Instance()