package io.synlabs.synvision.entity;

import io.synlabs.synvision.entity.core.Org;
import io.synlabs.synvision.entity.core.SynVisionUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

import static io.synlabs.synvision.service.BaseService.getAtccUser;


@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends LazyAuditable<SynVisionUser, Long> {

    @ManyToOne
    private Org org;

    @PrePersist
    private void preCreate() {
        this.setCreatedDate(LocalDateTime.now());

        SynVisionUser current = getCurrentUser();
        if (current != null) {
            this.setCreatedBy(current);
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.setLastModifiedDate(LocalDateTime.now());
        SynVisionUser current = getCurrentUser();
        if (current != null) {
            this.setLastModifiedBy(current);
        }

    }

    private SynVisionUser getCurrentUser() {
        return getAtccUser();
    }

    public void setId(Long Id) {
        super.setId(Id);
    }
}
