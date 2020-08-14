package io.synlabs.synvision.controller.atcc;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.controller.MediaUploadController;
import io.synlabs.synvision.service.AtccDataService;
import io.synlabs.synvision.views.*;
import io.synlabs.synvision.views.atcc.AtccEventFilterRequest;
import io.synlabs.synvision.views.atcc.AtccRawDataResponse;
import io.synlabs.synvision.views.atcc.AtccSummaryDataResponse;
import io.synlabs.synvision.views.atcc.CreateAtccEventRequest;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.common.ResponseWrapper;
import io.synlabs.synvision.views.common.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.*;

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
    @Secured(ATCC_READ)
    public ResponseWrapper<AtccRawDataResponse> findRawData(@RequestBody SearchRequest searchRequest) {
        return atccDataService.listRawData(searchRequest);
    }

    @PutMapping("summary")
    @Secured(ATCC_READ)
    public ResponseWrapper<AtccSummaryDataResponse> findSummaryData(@RequestBody SearchRequest searchRequest, @RequestParam String interval) {
        return atccDataService.listSummaryData(searchRequest, interval);
    }

    @PostMapping("/image")
    @Secured(ATCC_WRITE)
    public UploadFileResponse uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/video")
    @Secured(ATCC_WRITE)
    public UploadFileResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/event")
    @Secured(ATCC_WRITE)
    public void addEvent(@RequestBody CreateAtccEventRequest request) {
        atccDataService.addEvent(request);
    }

    @PostMapping("/events")
    @Secured(ATCC_READ)
    public PageResponse<AtccRawDataResponse> list(@RequestBody AtccEventFilterRequest request) {
        return atccDataService.list(request);
    }


}
