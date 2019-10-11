
import axios from 'axios';

class DeviceService {

    static Instance() {
        return new DeviceService()
    }

    getDevice() {
        return axios.get('/api/device');
    }

    getDeviceConfig() {
        return axios.get('/api/device/config');
    }

}

export default DeviceService.Instance()