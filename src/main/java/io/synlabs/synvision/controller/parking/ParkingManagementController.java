package io.synlabs.synvision.controller.parking;


import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.service.parking.ApmsService;
import io.synlabs.synvision.views.parking.ApmsFilterRequest;
import io.synlabs.synvision.views.parking.ApmsResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.PARKING_WRITE;


@RestController
@RequestMapping("/api/apms")
public class ParkingManagementController {
    private static final Logger logger = LoggerFactory.getLogger(ParkingManagementController.class);

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private ApmsService apmsService;

    @PostMapping("/events")
    @Secured(PARKING_WRITE)
    public PageResponse<ApmsResponse> eventsList(@RequestBody ApmsFilterRequest request) {
        return apmsService.eventsList(request);
    }

    @PostMapping("/event/status")
    @Secured(PARKING_WRITE)
    public ApmsResponse eventStatus(@RequestParam String vehicleNo) {
        return apmsService.eventStatus(vehicleNo);
    }

    @PostMapping("/check/in")
    @Secured(PARKING_WRITE)
    public void checkIn(@RequestParam String vehicleNo) {
        apmsService.checkIn(vehicleNo);
    }

    @PostMapping("/check/out")
    @Secured(PARKING_WRITE)
    public void checkOut(@RequestParam String vehicleNo) {
        apmsService.checkOut(vehicleNo);
    }

}

//TODO vehicle and ANPR image during checkin and checkout
