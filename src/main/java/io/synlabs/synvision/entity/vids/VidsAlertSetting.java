package io.synlabs.synvision.entity.vids;

import io.synlabs.synvision.enums.HighwayIncidentType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public class VidsAlertSetting extends AbstractPersistable<Long> {

    @Enumerated(EnumType.STRING)
    private HighwayIncidentType incidentType;

    private boolean enabled;

}
