package io.synlabs.synvision.entity.core;

import io.synlabs.synvision.views.OrgRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Org extends AbstractPersistable<Long> {
    private String name;
    private String legalName;
    private String logoFileName;

    public Org(OrgRequest request) {
        this.name = request.getName();
        this.legalName = request.getLegalName();
        this.logoFileName = request.getLogoFileName();
    }

    public void update(OrgRequest request) {
        this.name = request.getName();
        this.legalName = request.getLegalName();
        this.logoFileName = request.getLogoFileName();
    }
}
