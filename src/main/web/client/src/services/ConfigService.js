import axios from "./axios";

class ConfigService {

    static Instance() {
        return new ConfigService()
    }


    saveAnnotation(lines) {

        return axios.post("/save/annotation", JSON.stringify({
            lines: lines,
            // dataURL:dataURL

        }));
    }


}

export default ConfigService.Instance()