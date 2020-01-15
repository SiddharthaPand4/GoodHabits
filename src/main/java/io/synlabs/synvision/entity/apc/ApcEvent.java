package io.synlabs.synvision.entity.apc;

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
public class ApcEvent extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @Column(length = 50)
    private String eventImage;

    private boolean archived;

    @Column(length = 50)
    private String direction; //=> fwd or rev

    @Column(length = 50)
    private String source;

}
