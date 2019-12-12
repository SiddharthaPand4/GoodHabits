package io.synlabs.synvision.entity.anpr;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class HotListVehicle extends BaseEntity {
    @Column(length = 50)
    private String lpr;
    private boolean archived = false;
}
