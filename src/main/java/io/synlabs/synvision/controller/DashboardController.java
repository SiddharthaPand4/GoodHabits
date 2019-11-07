package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.DashboardService;
import io.synlabs.synvision.views.DashboardRequest;
import io.synlabs.synvision.views.DashboardResponse;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

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

}
