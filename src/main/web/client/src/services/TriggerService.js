
import axios from 'axios';
import {handleError, handleResponse} from "./UserService";

class TriggerService {

    static Instance() {
        return new TriggerService()
    }

    getTrigger() {
        return axios.get('/api/trigger/').then(handleResponse, handleError);
    }

    getTriggers() {
        return axios.get('/api/triggers').then(handleResponse, handleError);
    }

    createTrigger(trigger) {
        return axios.post('/api/trigger/', trigger).then(handleResponse, handleError);
    }

    saveTrigger(trigger) {
        return axios.put('/api/trigger/' + trigger.ID, trigger).then(handleResponse, handleError);
    }

    deleteTrigger(trigger) {
        return axios.delete('/api/trigger/' + trigger.ID).then(handleResponse, handleError);
    }
}

export default TriggerService.Instance()