package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.vids.VidsAlertSetting;
import io.synlabs.synvision.jpa.VidsAlertSettingRepository;
import io.synlabs.synvision.views.AlertSettingsRequest;
import io.synlabs.synvision.views.AlertSettingsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertSettingsService extends BaseService {

    private static final Logger logger  = LoggerFactory.getLogger(AlertSettingsService.class);

    @Autowired
    private VidsAlertSettingRepository vidsAlertSettingRepository;

    public List<AlertSettingsResponse> alertList() {
        return vidsAlertSettingRepository.findAll().stream().map(AlertSettingsResponse::new).collect(Collectors.toList());
    }

    public void saveAlertSettings(List<AlertSettingsRequest> alertSettingsRequestList) {
        vidsAlertSettingRepository.saveAll(alertSettingsRequestList.stream().map(VidsAlertSetting::new).collect(Collectors.toList()));
    }

}
