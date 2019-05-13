package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

import static io.synlabs.atcc.service.BaseService.getAtccUser;


@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends LazyAuditable<AtccUser, Long> {


    @PrePersist
    private void preCreate() {
        this.setCreatedDate(LocalDateTime.now());

        AtccUser current = getCurrentUser();
        if (current != null) {
            this.setCreatedBy(current);
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.setLastModifiedDate(LocalDateTime.now());
        AtccUser current = getCurrentUser();
        if (current != null) {
            this.setLastModifiedBy(current);
        }

    }

    private AtccUser getCurrentUser() {
        return getAtccUser();
    }

    public void setId(Long Id) {
        super.setId(Id);
    }
}
