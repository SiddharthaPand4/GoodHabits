package io.synlabs.synvision.entity.vids;

import io.synlabs.synvision.enums.HighwayIncidentType;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class VidsAlertSetting extends AbstractPersistable<Long> {

    @Enumerated(EnumType.STRING)
    private HighwayIncidentType incidentType;

    private boolean enabled;

}
