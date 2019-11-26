package io.synlabs.synvision.entity.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Org extends AbstractPersistable<Long> {
    private String name;
    private String legalName;
}
