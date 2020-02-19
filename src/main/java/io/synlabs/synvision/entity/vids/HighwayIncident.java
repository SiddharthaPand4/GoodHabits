package io.synlabs.synvision.entity.vids;

import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.enums.HighwayIncidentType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class HighwayIncident extends AbstractPersistable<Long> {

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date incidentDate;

    @Enumerated(EnumType.STRING)
    private HighwayIncidentType incidentType;

    private long timeStamp;

    @ManyToOne
    private Feed feed;

    @Column(length = 200)
    private String incidentImage;

    @Column(length = 200)
    private String incidentVideo;

    private boolean archived;

}
