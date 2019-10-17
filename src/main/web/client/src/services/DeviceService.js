import axios from './axios';
import { authHeader } from '../helpers/auth-header';
import { config } from '../helpers/config'

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