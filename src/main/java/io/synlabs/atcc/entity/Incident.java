package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Entity
public class Incident extends BaseEntity{

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(length = 50)
    private String eventTrigger;

    @Column(length = 10)
    private String eventDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventStart;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventEnd;

    private int eventDuration;

    @Column(length = 50)
    private String videoId;

    @Column(length = 50)
    private String imageId;
}
