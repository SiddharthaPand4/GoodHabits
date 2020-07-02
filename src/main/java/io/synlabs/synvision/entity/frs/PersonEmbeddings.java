package io.synlabs.synvision.entity.frs;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class PersonEmbeddings extends BaseEntity {

    @Column(length = 200)
    private String fullImage;

    @Column(length = 200)
    private String faceImage;

    @ManyToOne
    private RegisteredPerson person;

    @Column(length = 1000)
    private String embedding;

}
