package io.synlabs.synvision.controller.anpr;

import io.synlabs.synvision.service.AnprService;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.incident.IncidentRequest;
import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.synlabs.synvision.views.anpr.*;
/**
 * Created by itrs on 10/16/2019.
 */
@RestController
@RequestMapping("/api/traffic/incident/")
public class TrafficIncidentController {

    @Autowired
    private AnprService anprService;

    @PostMapping
    public PageResponse<AnprResponse> list(@RequestBody IncidentsFilterRequest request){
        return anprService.list(request);
    }

    @DeleteMapping("/{id}")
    public void archiveIncident(@PathVariable Long id ){
        anprService.archiveIncident(new IncidentRequest(id));
    }

}
