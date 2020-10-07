package io.synlabs.synvision.controller.avc;

import io.synlabs.synvision.service.avc.AvcReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report/avc")
public class AvcReportController {

    @Autowired
    private AvcReportService avcReportService;



}
