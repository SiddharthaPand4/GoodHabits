package io.synlabs.synvision.views;

import io.synlabs.synvision.entity.vids.VidsAlertSetting;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlertSettingsResponse implements Response {

    private String alertType;

    private Boolean status;

    public AlertSettingsResponse(VidsAlertSetting vidsAlertSetting) {
        this.alertType = vidsAlertSetting.getIncidentType().toString();
        this.status = vidsAlertSetting.isEnabled();
    }

}
