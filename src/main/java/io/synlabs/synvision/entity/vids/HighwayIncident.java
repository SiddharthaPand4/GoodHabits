package io.synlabs.synvision.entity.vids;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class HighwayIncident extends AbstractPersistable<Long> {

    @Temporal(TemporalType.TIMESTAMP)
    private Date incidentDate;

    private long timeStamp;

    private int lane;

    private BigDecimal speed;

    private int direction;

    private String type;

    private String feed;

    @Column(length = 50)
    private String incidentImage;

    @Column(length = 50)
    private String incidentVideo;

    private boolean archived;

}
