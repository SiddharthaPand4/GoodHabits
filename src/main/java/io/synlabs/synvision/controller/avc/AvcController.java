package io.synlabs.synvision.controller.avc;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.controller.MediaUploadController;
import io.synlabs.synvision.service.avc.AvcDataService;
import io.synlabs.synvision.util.LongObfuscator;
import io.synlabs.synvision.views.UploadFileResponse;
import io.synlabs.synvision.views.avc.AvcEventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.AVC_SURVEY_WRITE;

@RestController
public class AvcController extends MediaUploadController {

    @Autowired
    private AvcDataService avcDataService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @PostMapping("/image")
    @Secured(AVC_SURVEY_WRITE)
    public UploadFileResponse uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("surveyId") Long surveyId) {
        String tag = avcDataService.surveyFolder(LongObfuscator.INSTANCE.unobfuscate(surveyId));
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/video")
    @Secured(AVC_SURVEY_WRITE)
    public UploadFileResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("surveyId") Long surveyId) {
        String tag = avcDataService.surveyFolder(LongObfuscator.INSTANCE.unobfuscate(surveyId));
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/event")
    @Secured(AVC_SURVEY_WRITE)
    public void addEvent(@RequestBody AvcEventRequest request) {
        avcDataService.addEvent(request);
    }

}