import axios from "./axios"

class AvcService {

    static Instance() {
        return new AvcService()
    }

    saveSurvey(survey) {
        return axios.post('/api/survey/', survey)
    }

}

export default AvcService.Instance()