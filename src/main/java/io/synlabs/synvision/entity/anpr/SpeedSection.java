package io.synlabs.synvision.entity.anpr;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.entity.core.Feed;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
public class SpeedSection extends BaseEntity {

    private String entrySite;

    private String exitSite;

    private String name;

    private int sectionDistance;    //in meters

    private double maxSpeed; //in kmph
}


/*

this is not good enough, consider a case where entry gentry has three cameras and exit gentry also has three cameras
we cannot pair cameras like this, but we need to pair on location!

 */