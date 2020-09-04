package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.DashboardService;
import io.synlabs.synvision.views.DashboardRequest;
import io.synlabs.synvision.views.DashboardResponse;
import io.synlabs.synvision.views.anpr.AnprVehicleCountResponse;
import io.synlabs.synvision.views.atcc.AtccVehicleCountResponse;
import io.synlabs.synvision.views.incident.IncidentCountResponse;
import io.synlabs.synvision.views.incident.IncidentGroupCountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.*;

/**
 * Created by itrs on 10/23/2019.
 */
@RestController
@RequestMapping("/api/dashboard/")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("vehiclescount/datefilter")
    public List<DashboardResponse> getTotalNoOfVehiclesByDateFilter(@RequestBody DashboardRequest request){
        return dashboardService.getTotalNoOfVehiclesByDateFilter(request);
    }

    @PostMapping("atcc/vehicle/count")
    @Secured(ATCC_READ)
    public List<AtccVehicleCountResponse> getAtccVehicleCount(@RequestBody DashboardRequest request){
        return dashboardService.getAtccVehicleCount(request);
    }

    @PostMapping("incidentCount")
    @Secured(INCIDENT_COUNT_READ)
    public List<IncidentCountResponse> getIncidentCount(@RequestBody DashboardRequest request) {
        return dashboardService.getIncidentCountForAllTypes(request);
    }

    @PostMapping("incident/vehicle/count")
    @Secured(INCIDENT_READ)
    public IncidentGroupCountResponse getIncidentVehicleCount(@RequestBody DashboardRequest request){
        return dashboardService.getIncidentsCount(request);
    }

    @PostMapping("anpr/vehicle/count")
    @Secured(ANPR_READ)
    public List<AnprVehicleCountResponse> getAnprCount(@RequestBody DashboardRequest request){
        return dashboardService.getAnprCount(request);
    }



}
