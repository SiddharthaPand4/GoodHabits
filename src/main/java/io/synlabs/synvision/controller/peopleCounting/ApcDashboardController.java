package io.synlabs.synvision.controller.peopleCounting;

import io.synlabs.synvision.service.ApcDashboardService;
import io.synlabs.synvision.views.apc.ApcDashboardPeakHourResponse;
import io.synlabs.synvision.views.apc.ApcDashboardRequest;
import io.synlabs.synvision.views.apc.ApcDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/apc/dashboard/")
public class ApcDashboardController {
    @Autowired
    private ApcDashboardService apcDashboardService;


    @PostMapping("peoplecount")
    public List<ApcDashboardResponse> getApcPeopleCount(@RequestBody ApcDashboardRequest request){
        return apcDashboardService.getApcPeopleCount(request);
    }

    @PostMapping("peakhour")
    public List<ApcDashboardPeakHourResponse> getApcPeakHour(@RequestBody ApcDashboardRequest request){
        return apcDashboardService.getApcPeakHour(request);
    }
}
