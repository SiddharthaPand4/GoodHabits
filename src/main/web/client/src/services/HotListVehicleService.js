import axios from "./axios";

class HotListVehicleService {

    static Instance() {
        return new HotListVehicleService()
    }

    getHotListedVehicles(filter) {
        return axios.post('/api/hotlist/vehicle/list', filter);
    }

    saveHotListedVehicle(vehicle) {
        return axios.post('/api/hotlist/vehicle/save', vehicle);
    }


}

export default HotListVehicleService.Instance()