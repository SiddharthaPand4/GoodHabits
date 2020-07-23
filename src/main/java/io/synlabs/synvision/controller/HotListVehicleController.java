package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.HotListVehicleService;
import io.synlabs.synvision.views.hotlist.HotListVehicleFilterRequest;
import io.synlabs.synvision.views.hotlist.HotListVehicleRequest;
import io.synlabs.synvision.views.hotlist.HotListVehicleResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static io.synlabs.synvision.auth.LicenseServerAuth.Privileges.*;

@RestController
@RequestMapping("/api/hotlist/vehicle")

public class HotListVehicleController {

    @Autowired
    private HotListVehicleService hotListVehicleService;

    @PostMapping("/list")
    @Secured(HOTLIST_READ)
    public PageResponse<HotListVehicleResponse> list(@RequestBody HotListVehicleFilterRequest request) {
        return hotListVehicleService.listHotListedVehicles(request);
    }

    @PostMapping("/save")
    @Secured(HOTLIST_WRITE)
    public HotListVehicleResponse save(@RequestBody HotListVehicleRequest request) {
        return hotListVehicleService.save(request);
    }
}
