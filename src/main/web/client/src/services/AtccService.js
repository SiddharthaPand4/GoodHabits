import axios from "./axios";

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

}

export default AtccService.Instance()