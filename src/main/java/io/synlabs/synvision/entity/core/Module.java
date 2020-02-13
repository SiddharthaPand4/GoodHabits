package io.synlabs.synvision.entity.core;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Module extends BaseEntity {

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    private String menuKey;

    private boolean enabled;
}
