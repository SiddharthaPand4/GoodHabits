package io.synlabs.synvision.controller.vids;

import io.synlabs.synvision.service.VidsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/public/vids")
public class VidsFileController {

    private static final Logger logger = LoggerFactory.getLogger(VidsFileController.class);

    @Autowired
    private VidsService vidsService;

    @GetMapping("/image/{id}/image.jpg")
    public ResponseEntity<Resource> downloadVehicleImage(@PathVariable Long id, HttpServletRequest request) throws IOException {

        Resource resource = vidsService.downloadIncidentImage(id);
        return send(resource, request);
    }

    @GetMapping("/video/{id}/video.mp4")
    public ResponseEntity<Resource> downloadVehicleVideo(@PathVariable Long id, HttpServletRequest request) throws IOException {

        Resource resource = vidsService.downloadIncidentVideo(id);
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

}
