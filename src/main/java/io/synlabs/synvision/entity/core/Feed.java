package io.synlabs.synvision.entity.core;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Feed extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String location;

    @Column(nullable = false, length = 50)
    private String site;

    @Column(nullable = false, length = 50)
    private String url;

    private boolean checkSectionSpeed;

}
/*
 name -> site -> location
 e.g.
 CAM 1 -> Maloya 1 -> Lucknow
 */