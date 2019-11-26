package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.IncidentService;
import io.synlabs.synvision.views.incident.IncidentRequest;
import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import io.synlabs.synvision.views.incident.IncidentsResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by itrs on 10/16/2019.
 */
@RestController
@RequestMapping("/api/incident")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @PostMapping
    public PageResponse<IncidentsResponse> list(@RequestBody IncidentsFilterRequest request){
        return incidentService.list(request);
    }

    @DeleteMapping("/{id}")
    public void archiveIncident(@PathVariable Long id ){
        incidentService.archiveIncident(new IncidentRequest(id));
    }

}
