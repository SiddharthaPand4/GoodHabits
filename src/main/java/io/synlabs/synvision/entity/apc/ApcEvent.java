package io.synlabs.synvision.entity.apc;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ApcEvent extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    private boolean archived;

    @Column(length = 50)
    private String direction;

    @Column(length = 50)
    private String source;

}
