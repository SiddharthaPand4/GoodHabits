package io.synlabs.synvision.entity.frs;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.enums.AccessType;
import io.synlabs.synvision.enums.PersonType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public class RegisteredPerson extends BaseEntity {

    @Column(length = 100)
    private String uid;

    @Column(length = 200)
    private String fullImage;

    @Column(length = 200)
    private String faceImage;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    @Column(length = 50)
    private String pid;

    @Column(length = 20)
    private String name;

    private boolean active;

}
