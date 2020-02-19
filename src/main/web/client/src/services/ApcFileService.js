import axios from "./axios";

class ApcFileService{
static Instance() {
        return new ApcFileService()
}

getPeopleData(filter) {
        return axios.post('/api/apc/pplData', filter);
}


 archiveEvent(event) {
        return axios.delete('/api/apc/'+ event.id);
    }
}
export default ApcFileService.Instance()