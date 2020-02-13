package io.synlabs.synvision.controller.atcc;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.controller.MediaUploadController;
import io.synlabs.synvision.ex.AuthException;
import io.synlabs.synvision.service.AtccDataService;
import io.synlabs.synvision.views.*;
import io.synlabs.synvision.views.atcc.AtccRawDataResponse;
import io.synlabs.synvision.views.atcc.AtccSummaryDataResponse;
import io.synlabs.synvision.views.atcc.CreateAtccEventRequest;
import io.synlabs.synvision.views.common.ResponseWrapper;
import io.synlabs.synvision.views.common.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/atcc/")
public class AtccController extends MediaUploadController {

    private static final Logger logger = LoggerFactory.getLogger(AtccController.class);

    private final AtccDataService atccDataService;

    private String uploadKey;

    @Autowired
    private FileStorageProperties fileStorageProperties;


    public AtccController(AtccDataService atccDataService, FileStorageProperties fileStorageProperties) {
        this.atccDataService = atccDataService;
        this.uploadKey = fileStorageProperties.getUploadKey();
    }

    @PutMapping("raw")
    public ResponseWrapper<AtccRawDataResponse> findRawData(@RequestBody SearchRequest searchRequest) {
        return atccDataService.listRawData(searchRequest);
    }

    @PutMapping("summary")
    public ResponseWrapper<AtccSummaryDataResponse> findSummaryData(@RequestBody SearchRequest searchRequest, @RequestParam String interval) {

        return atccDataService.listSummaryData(searchRequest, interval);
    }

    @PostMapping("/import/csv")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("key") String authKey, @RequestParam("tag") String tag) {

        logger.info("File uploaded, now importing..{} with tag {}", file.getOriginalFilename(), tag);
        if (uploadKey != null && !uploadKey.equals(authKey)) {
            logger.error("Keys not matching! supplied - {}", authKey);
            throw new AuthException("Not allowed");
        }

        String fileName = atccDataService.importFile(file, tag);

        return new UploadFileResponse(fileName, file.getContentType(), file.getSize(), tag);
    }

    @PostMapping("/import/video")
    public UploadFileResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("key") String authKey, @RequestParam("tag") String tag) {

        if (uploadKey != null && !uploadKey.equals(authKey)) {
            logger.error("Keys not matching! supplied - {}", authKey);
            throw new AuthException("Not allowed");
        }

        logger.info("video File uploaded, now importing..{}", file.getOriginalFilename());
        String fileName = atccDataService.importVideo(file, tag);

        return new UploadFileResponse(fileName,  file.getContentType(), file.getSize(), tag);
    }

    @PostMapping("/image")
    public UploadFileResponse uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/video")
    public UploadFileResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/event")
    public void addEvent(@RequestBody CreateAtccEventRequest request) {
        atccDataService.addEvent(request);
    }

}
