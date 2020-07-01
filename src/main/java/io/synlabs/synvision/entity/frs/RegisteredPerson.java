package io.synlabs.synvision.entity.frs;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.enums.PersonType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class RegisteredPerson extends BaseEntity {

    @Column(length = 200)
    private String fullImage;

    @Column(length = 200)
    private String faceImage;

    private PersonType personType;

    @Column(length = 20)
    private String pid;

    @Column(length = 20)
    private String name;

    private boolean active;

}
