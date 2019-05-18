package io.synlabs.atcc.controller;

import io.synlabs.atcc.service.AtccDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.core.io.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class AtccVideoController {

    private static final Logger logger = LoggerFactory.getLogger(AtccVideoController.class);

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

    private ResponseEntity<Resource> send(Resource resource, HttpServletRequest request) {
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
}
