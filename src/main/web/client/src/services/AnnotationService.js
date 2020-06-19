import axios from "./axios";

class AnnotationService {

    static Instance() {
        return new AnnotationService()
    }


    saveAnnotation(lines) {

        return axios.post("/save/annotation", JSON.stringify({
            lines: lines,
            // dataURL:dataURL

        }));
    }

    startFeed(feedUrl) {
        return axios.get("/save/start?feedUrl=" + feedUrl);
    }

    stopFeed() {
        return axios.get("/save/stop/feed");
    }
}

export default AnnotationService.Instance()