package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.IncidentService;
import io.synlabs.synvision.views.IncidentRequest;
import io.synlabs.synvision.views.IncidentsFilterRequest;
import io.synlabs.synvision.views.IncidentsResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static io.synlabs.synvision.auth.LicenseServerAuth.Privileges.INCIDENT_READ;
import static io.synlabs.synvision.auth.LicenseServerAuth.Privileges.INCIDENT_WRITE;

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
