package io.synlabs.synvision.entity.vids;

import io.synlabs.synvision.enums.HighwayIncidentType;
import io.synlabs.synvision.views.AlertSettingsRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VidsAlertSetting extends AbstractPersistable<Long> {

    @Enumerated(EnumType.STRING)
    private HighwayIncidentType incidentType;

    private boolean enabled;

    public VidsAlertSetting(AlertSettingsRequest alertSettingsRequest) {
        this.incidentType = HighwayIncidentType.valueOf(alertSettingsRequest.getAlertType());
        this.enabled = alertSettingsRequest.getStatus();
    }

}
