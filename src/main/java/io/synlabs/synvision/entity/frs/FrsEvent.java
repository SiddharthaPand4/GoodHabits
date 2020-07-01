package io.synlabs.synvision.entity.frs;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.entity.core.SynVisionUser;
import io.synlabs.synvision.enums.FrsEventType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class FrsEvent extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Enumerated(EnumType.STRING)
    private FrsEventType type;

    @ManyToOne
    private RegisteredPerson person;

    @Column(length = 200)
    private String fullImage;

    @Column(length = 200)
    private String faceImage;

    private boolean archived;

    private boolean alert;

    private boolean acknowledged;

    @ManyToOne
    private SynVisionUser acknowledgedBy;
}
