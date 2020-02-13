package io.synlabs.synvision.controller.vids;

import io.synlabs.synvision.service.VidsService;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.vids.VidsFilterRequest;
import io.synlabs.synvision.views.vids.VidsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vids/")
public class VidsController {

    @Autowired
    private VidsService vidsService;


    @PostMapping("/incidents")
    public PageResponse<VidsResponse> list(@RequestBody VidsFilterRequest request) {
        return vidsService.listIncidents(request);
    }
}
