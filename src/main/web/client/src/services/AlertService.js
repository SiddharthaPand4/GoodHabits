import axios from './axios'

class AlertService {

    static Instance () {
        return new AlertService()
    }

    fetchAlertTypes() {
        return axios.get("pending end point")
    }

    saveAlertSettings(config) {
        return axios.post("save alert config endpoint", config)
    }

}

export default AlertService.Instance()