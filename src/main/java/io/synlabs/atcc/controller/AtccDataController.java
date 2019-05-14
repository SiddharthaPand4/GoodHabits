package io.synlabs.atcc.controller;

import io.synlabs.atcc.entity.AtccRawData;
import io.synlabs.atcc.entity.AtccSummaryData;
import io.synlabs.atcc.service.AtccDataService;
import io.synlabs.atcc.views.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class AtccDataController {

    private static final Logger logger = LoggerFactory.getLogger(AtccDataController.class);

    @Autowired
    private AtccDataService dataService;

    @GetMapping("raw")
    public List<AtccRawData> findRawData() {
        return dataService.listRawData();
    }

    @GetMapping("summary")
    public List<AtccSummaryData> findSummaryData() {
        return dataService.listSummaryData();
    }

    @PostMapping("/import")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {

        logger.info("File uploaded, now importing..{}", file.getOriginalFilename());
        String fileName = dataService.importFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }
}
