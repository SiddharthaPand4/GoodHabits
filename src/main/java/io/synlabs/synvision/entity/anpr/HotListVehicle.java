package io.synlabs.synvision.entity.anpr;

import io.synlabs.synvision.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class HotListVehicle extends BaseEntity {
    @Column(length = 50)
    private String lpr;
}
