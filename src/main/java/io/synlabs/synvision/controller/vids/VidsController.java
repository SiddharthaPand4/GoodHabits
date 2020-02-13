package io.synlabs.synvision.controller.vids;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.controller.MediaUploadController;
import io.synlabs.synvision.service.VidsService;
import io.synlabs.synvision.views.UploadFileResponse;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.vids.CreateIncidentRequest;
import io.synlabs.synvision.views.vids.VidsFilterRequest;
import io.synlabs.synvision.views.vids.VidsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vids/")
public class VidsController extends MediaUploadController {

    private static final Logger logger = LoggerFactory.getLogger(VidsController.class);

    @Autowired
    private VidsService vidsService;

    @Autowired
    private FileStorageProperties fileStorageProperties;


    @PostMapping("/incidents")
    public PageResponse<VidsResponse> list(@RequestBody VidsFilterRequest request) {
        return vidsService.listIncidents(request);
    }

    @DeleteMapping("/{id}")
    public void archiveIncident(@PathVariable Long id) {
        vidsService.archiveIncident(id);
    }

    @PostMapping("/incident")
    public void addIncident(@RequestBody CreateIncidentRequest request) {
        vidsService.addIncident(request);
    }

    @PostMapping("/image")
    public UploadFileResponse uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/video")
    public UploadFileResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }
}
