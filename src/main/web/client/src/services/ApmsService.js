import axios from "./axios";

class ApmsService {

    static Instance() {
        return new ApmsService()
    }

    checkIn(vehicleNo) {
        return axios.post('/api/apms/check/in?vehicleNo=' + vehicleNo);
    }

    checkOut(vehicleNo) {
        return axios.post('/api/apms/check/out?vehicleNo=' + vehicleNo);
    }

    getEvents(filter) {
        return axios.post('/api/apms/events', filter);
    }

    getEventStatus(vehicleNo) {
        return axios.post('/api/apms/event/status?vehicleNo=' + vehicleNo);
    }

    getParkingSlotStats() {
        return axios.get('/api/apms/guidance/stats');
    }

    getCheckedInVehiclesCount() {
        return axios.get('/api/apms/guidance/checked-in/current/count');
    }

    getSlots() {
        return axios.get('/api/apms/guidance/slots?lot=lucknow')
    }

    updateSlot(slot, status) {
        return axios.post('/api/apms/guidance/slots', {lot:'lucknow', slot:slot, status:status})
    }
}
export default ApmsService.Instance()