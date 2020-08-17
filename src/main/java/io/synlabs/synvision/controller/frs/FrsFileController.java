package io.synlabs.synvision.controller.frs;

import io.synlabs.synvision.service.AtccDataService;
import io.synlabs.synvision.service.frs.FrsEventService;
import io.synlabs.synvision.service.frs.RegisteredPersonService;
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
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping("/public/frs")
public class FrsFileController {

    private static final Logger logger = LoggerFactory.getLogger(FrsFileController.class);

    @Autowired
    private FrsEventService eventService;

    @Autowired
    private RegisteredPersonService personService;

    @GetMapping("/person/face/{uid}/image.jpg")
    public ResponseEntity<Resource> downloadFaceImage(@PathVariable String uid, HttpServletRequest request) throws IOException {

        Resource resource = personService.downloadFaceImage(uid);
        return send(resource, request);
    }

    @GetMapping("/person/full/{uid}/image.jpg")
    public ResponseEntity<Resource> downloadPersonImage(@PathVariable String uid, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = personService.downloadPersonImage(uid);
        return send(resource, request);
    }


    @GetMapping("/event/face/{uid}/image.jpg")
    public ResponseEntity<Resource> downloadEventFaceImage(@PathVariable String uid, HttpServletRequest request) throws IOException {

        Resource resource = eventService.downloadFaceImage(uid);
        return send(resource, request);
    }

    @GetMapping("/event/full/{uid}/image.jpg")
    public ResponseEntity<Resource> downloadEventPersonImage(@PathVariable String uid, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = eventService.downloadPersonImage(uid);
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

}
