package io.synlabs.synvision.controller.anpr;

import io.synlabs.synvision.service.AtccDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
public class AnprFileController {

    private static final Logger logger = LoggerFactory.getLogger(AnprFileController.class);

    @Autowired
    private AtccDataService dataService;

    @GetMapping("/screenshot/{id}")
    public ResponseEntity<Resource> downloadScrenshot(@PathVariable Long id, HttpServletRequest request) throws IOException {

        Resource resource = dataService.getScreenshot(id);
        return send(resource, request);
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = dataService.loadFileAsResource(id);
        return send(resource, request);
    }

    public ResponseEntity<Resource> send(Resource resource, HttpServletRequest request) {
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public ResponseEntity<Resource> send(File file, HttpServletRequest request) throws FileNotFoundException {
        // Try to determine file's content type
        String contentType = null;
        contentType = request.getServletContext().getMimeType(file.getAbsolutePath());

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getPath() + "\"")
                .body(new InputStreamResource(new FileInputStream(file)));
    }

    @GetMapping("/csv/")
    public ResponseEntity<Resource> exportCSV(HttpServletRequest request) throws Exception {
        File file = dataService.streamRawData();
        return send(file, request);
    }

    @GetMapping("/csv/summary/{interval}")
    public ResponseEntity<Resource> exportSummaryCSV(HttpServletRequest request, @PathVariable String interval) throws Exception {
        Resource resource = dataService.makeSummaryData(interval);
        return send(resource, request);
    }

}
