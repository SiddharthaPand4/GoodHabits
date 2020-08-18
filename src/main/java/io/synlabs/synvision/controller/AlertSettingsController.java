package io.synlabs.synvision.controller;

import io.synlabs.synvision.jpa.VidsAlertSettingRepository;
import io.synlabs.synvision.service.AlertSettingsService;
import io.synlabs.synvision.views.AlertSettingsRequest;
import io.synlabs.synvision.views.AlertSettingsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setting/alerts")
public class AlertSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(AlertSettingsController.class);

    @Autowired
    private AlertSettingsService alertSettingsService;

    @GetMapping("/list")
    public List<AlertSettingsResponse> alertList() {
        return alertSettingsService.alertList();
    }

    @PutMapping("/update")
    public void saveAlertSettings(@RequestBody List<AlertSettingsRequest> alertSettingsRequestList) {
        alertSettingsService.saveAlertSettings(alertSettingsRequestList);
    }

}
