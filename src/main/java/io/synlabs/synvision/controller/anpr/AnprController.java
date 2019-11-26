package io.synlabs.synvision.controller.anpr;

import io.synlabs.synvision.service.AnprService;
import io.synlabs.synvision.views.anpr.AnprRequest;
import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.IncidentsFilterRequest;
import io.synlabs.synvision.views.anpr.CreateAnprRequest;
import io.synlabs.synvision.views.common.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by itrs on 10/21/2019.
 */
@RestController
@RequestMapping("/api/anpr")
public class AnprController {

    @Autowired
    private AnprService anprService;

    @PostMapping("/events")
    public PageResponse<AnprResponse> list(@RequestBody IncidentsFilterRequest request) {
        return anprService.list(request);
    }

    @DeleteMapping("/{id}")
    public void archiveAnpr(@PathVariable Long id) {
        anprService.archiveAnpr(new AnprRequest(id));
    }

    @PostMapping
    public void addAnprEvent(@RequestBody CreateAnprRequest request) {

    }

}
