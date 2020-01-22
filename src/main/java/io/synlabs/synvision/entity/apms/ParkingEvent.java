package io.synlabs.synvision.entity.apms;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
public class ParkingEvent extends BaseEntity {

    @Column(length = 50)
    private String vehiclNo;

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventInDate;

    private String eventInScreenshot;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventOutDate;

    private String eventOutScreenshot;

    private boolean archived;

    @Column(length = 50)
    private String source;


}
