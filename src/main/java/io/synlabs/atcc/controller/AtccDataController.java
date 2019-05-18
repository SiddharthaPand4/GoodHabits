package io.synlabs.atcc.controller;

import io.synlabs.atcc.config.FileStorageProperties;
import io.synlabs.atcc.ex.AuthException;
import io.synlabs.atcc.service.AtccDataService;
import io.synlabs.atcc.views.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.synlabs.atcc.views.AtccRawDataResponse;
import io.synlabs.atcc.views.AtccSummaryDataResponse;
import io.synlabs.atcc.views.ResponseWrapper;
import io.synlabs.atcc.views.SearchRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/data")
public class AtccDataController {

    private static final Logger logger = LoggerFactory.getLogger(AtccDataController.class);

    private final AtccDataService dataService;

    private String uploadKey;

    public AtccDataController(AtccDataService dataService, FileStorageProperties fileStorageProperties) {
        this.dataService = dataService;
        this.uploadKey = fileStorageProperties.getUploadKey();
    }

    @PutMapping("raw")
    public ResponseWrapper<AtccRawDataResponse> findRawData(@RequestBody SearchRequest searchRequest) {
        return dataService.listRawData(searchRequest);
    }

    @PutMapping("summary")
    public ResponseWrapper<AtccSummaryDataResponse> findSummaryData(@RequestBody SearchRequest searchRequest, @RequestParam String interval) {

        return dataService.listSummaryData(searchRequest, interval);
    }

    @PostMapping("/import/csv")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("key") String authKey, @RequestParam("tag") String tag) {

        logger.info("File uploaded, now importing..{}", file.getOriginalFilename());
        if (uploadKey != null && !uploadKey.equals(authKey)) {
            logger.error("Keys not matching! supplied - {}", authKey);
            throw new AuthException("Not allowed");
        }

        String fileName = dataService.importFile(file, tag);

        return new UploadFileResponse(fileName, file.getContentType(), file.getSize(), tag);
    }

    @PostMapping("/import/video")
    public UploadFileResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("key") String authKey, @RequestParam("tag") String tag) {

        if (uploadKey != null && !uploadKey.equals(authKey)) {
            logger.error("Keys not matching! supplied - {}", authKey);
            throw new AuthException("Not allowed");
        }

        logger.info("video File uploaded, now importing..{}", file.getOriginalFilename());
        String fileName = dataService.importVideo(file, tag);

        return new UploadFileResponse(fileName,  file.getContentType(), file.getSize(), tag);
    }

}
