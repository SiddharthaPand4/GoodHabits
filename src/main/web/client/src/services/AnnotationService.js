import axios from "./axios";

class AnnotationService {

    static Instance() {
        return new AnnotationService()
    }

   saveAnnotation(lines){
     // return axios.post("/save/annotation?lines="+JSON.stringify({
     //     lines: lines
     // }));
       return axios.post("/save/annotation", JSON.stringify({
           lines: lines
       }));
   }

}

export default AnnotationService.Instance()