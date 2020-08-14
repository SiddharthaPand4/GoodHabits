package io.synlabs.synvision.controller.vids;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.controller.MediaUploadController;
import io.synlabs.synvision.service.VidsDashboardService;
import io.synlabs.synvision.service.VidsService;
import io.synlabs.synvision.views.UploadFileResponse;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.vids.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.VIDS_READ;
import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.VIDS_WRITE;

@RestController
@RequestMapping("/api/vids/")
public class VidsController extends MediaUploadController {

    private static final Logger logger = LoggerFactory.getLogger(VidsController.class);

    @Autowired
    private VidsService vidsService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private VidsDashboardService dashboardService;

    @PostMapping("/incidents")
    @Secured(VIDS_READ)
    public PageResponse<VidsResponse> list(@RequestBody VidsFilterRequest request) {
        return vidsService.listIncidents(request);
    }

    @DeleteMapping("/{id}")
    @Secured(VIDS_WRITE)
    public void archiveIncident(@PathVariable Long id) {
        vidsService.archiveIncident(id);
    }

    @PostMapping("/incident")
    @Secured(VIDS_WRITE)
    public void addIncident(@RequestBody CreateIncidentRequest request) {
        vidsService.addIncident(request);
    }

    @PostMapping("/flow")
    @Secured(VIDS_WRITE)
    public void updateFlow(@RequestBody TrafficFlowUpdateRequest request) {
        vidsService.updateFlow(request);
    }

    @PostMapping("/flow/image")
    @Secured(VIDS_WRITE)
    public UploadFileResponse uploadFlowImage(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/image")
    @Secured(VIDS_WRITE)
    public UploadFileResponse uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @PostMapping("/video")
    @Secured(VIDS_WRITE)
    public UploadFileResponse uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        return UploadFile(file, tag, fileStorageProperties);
    }

    @GetMapping("/stats")
    @Secured(VIDS_READ)
    public VidsDashboardResponse getStats() {
        return dashboardService.dashboardstats();
    }


}
