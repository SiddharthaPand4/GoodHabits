import axios from "./axios";

class DashboardService {

    static Instance() {
        return new DashboardService()
    }

    getTotalNoOfVehiclesBetweenTwoDates(filter) {
        return axios.post('/api/dashboard/vehiclescount/datefilter',filter);
    }

    getTotalNoOfVehiclesBySelectedDate(filter) {
        return axios.post('/api/dashboard/vehiclescount/date',filter);
    }

    getTotalNoOfVehiclesForLastMonths(filter) {
        return axios.post('/api/dashboard/vehiclescount/month',filter);
    }

}

export default DashboardService.Instance()