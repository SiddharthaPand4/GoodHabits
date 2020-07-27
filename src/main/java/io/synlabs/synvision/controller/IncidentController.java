package io.synlabs.synvision.controller;

import io.synlabs.synvision.enums.HighwayIncidentType;
import io.synlabs.synvision.service.AnprService;
import io.synlabs.synvision.service.IncidentService;
import io.synlabs.synvision.views.anpr.AnprFilterRequest;
import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.anpr.IncidentRepeatCount;
import io.synlabs.synvision.views.incident.IncidentRequest;
import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import io.synlabs.synvision.views.incident.IncidentsResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static io.synlabs.synvision.auth.LicenseServerAuth.Privileges.*;

/**
 * Created by itrs on 10/16/2019.
 */
@RestController
@RequestMapping("/api/incident")

public class IncidentController {

    private static final Logger logger = LoggerFactory.getLogger(IncidentController.class);
    @Autowired
    private IncidentService incidentService;
    @Autowired
    private AnprService anprService;
    @PostMapping
    @Secured(INCIDENT_READ)
    public PageResponse<IncidentsResponse> list(@RequestBody IncidentsFilterRequest request){
        return incidentService.list(request);
    }

    @DeleteMapping("/{id}")
    @Secured(INCIDENT_WRITE)
    public void archiveIncident(@PathVariable Long id ){
        incidentService.archiveIncident(new IncidentRequest(id));
    }

    @PostMapping("/repeated/reverse")
    @Secured(ANPR_READ)
    public PageResponse<IncidentRepeatCount> listRepeatedIncidents(@RequestBody AnprFilterRequest request) {
        return anprService.listRepeatedIncidents(request);
    }

    @PostMapping("/repeated/helmet-missing")
    @Secured(ANPR_READ)
    public PageResponse<IncidentRepeatCount> listRepeatedHelmetMissingIncidents(@RequestBody AnprFilterRequest request) {
        return anprService.listRepeatedHelmetMissingIncidents(request);
    }

    @PostMapping("/timeline")
    @Secured(ANPR_READ)
    public PageResponse<AnprResponse> getIncidentsTimeline(@RequestBody AnprFilterRequest request) {
        return anprService.getIncidentsTimeline(request);
    }

    @GetMapping("/get/types")
    @Secured(INCIDENT_READ)
    public ArrayList<String> GetAllTypes()
    {
        ArrayList<String> list=new ArrayList<String>();
        for(HighwayIncidentType t:HighwayIncidentType.values())
        {list.add(t.toString());}
        return list;
    }
}
