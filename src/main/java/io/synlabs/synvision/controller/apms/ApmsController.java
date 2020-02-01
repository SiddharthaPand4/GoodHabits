package io.synlabs.synvision.controller.apms;


import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.views.apms.ApmsFilterRequest;
import io.synlabs.synvision.views.apms.ApmsResponse;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.service.ApmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/apms")


public class ApmsController {
    private static final Logger logger = LoggerFactory.getLogger(ApmsController.class);

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private ApmsService apmsService;

    @PostMapping("/events")
    public PageResponse<ApmsResponse> eventsList(@RequestBody ApmsFilterRequest request) {
        return apmsService.eventsList(request);
    }

    @PostMapping("/event/status")
    public ApmsResponse eventStatus(@RequestParam String vehicleNo) {
        return apmsService.eventStatus(vehicleNo);
    }
    @PostMapping("/check/in")
    public void checkIn(@RequestParam String vehicleNo) {
         apmsService.checkIn(vehicleNo);
    }

    @PostMapping("/check/out")
    public void checkOut(@RequestParam String vehicleNo) {
         apmsService.checkOut(vehicleNo);
    }

}
