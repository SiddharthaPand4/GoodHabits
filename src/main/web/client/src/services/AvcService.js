import axios from "./axios"

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
        //report api
    }

}

export default AvcService.Instance()