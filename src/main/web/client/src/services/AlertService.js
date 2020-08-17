import axios from './axios'

class AlertService {

    static Instance () {
        return new AlertService()
    }

    fetchAlertTypes() {
        return axios.get("/api/setting/alerts/list")
    }

    saveAlertSettings(config) {
        return axios.post("/api/setting/alerts/update", config)
    }

}

export default AlertService.Instance()