
import axios from 'axios';

class TriggerService {

    static Instance() {
        return new TriggerService()
    }

    getTrigger() {
        return axios.get('/api/trigger/');
    }

    getTriggers() {
        return axios.get('/api/triggers');
    }

    createTrigger(trigger) {
        return axios.post('/api/trigger/', trigger);
    }

    saveTrigger(trigger) {
        return axios.put('/api/trigger/' + trigger.ID, trigger);
    }

    deleteTrigger(trigger) {
        return axios.delete('/api/trigger/' + trigger.ID);
    }
}

export default TriggerService.Instance()