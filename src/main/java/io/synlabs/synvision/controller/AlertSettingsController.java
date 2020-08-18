package io.synlabs.synvision.controller;

import io.synlabs.synvision.auth.SynvisionAuth;
import io.synlabs.synvision.jpa.VidsAlertSettingRepository;
import io.synlabs.synvision.service.AlertSettingsService;
import io.synlabs.synvision.views.AlertSettingsRequest;
import io.synlabs.synvision.views.AlertSettingsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.ALERT_SETTING_READ;
import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.ALERT_SETTING_WRITE;

@RestController
@RequestMapping("/api/setting/alerts")
public class AlertSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(AlertSettingsController.class);

    @Autowired
    private AlertSettingsService alertSettingsService;

    @GetMapping("/list")
    @Secured(ALERT_SETTING_READ)
    public List<AlertSettingsResponse> alertList() {
        return alertSettingsService.alertList();
    }

    @PutMapping("/update")
    @Secured(ALERT_SETTING_WRITE)
    public void saveAlertSettings(@RequestBody List<AlertSettingsRequest> alertSettingsRequestList) {
        alertSettingsService.saveAlertSettings(alertSettingsRequestList);
    }

}
