package com.piedpiper.goodhabits.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

import static com.piedpiper.goodhabits.service.BaseService.getGoodHabitsUser;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends LazyAuditable<GoodHabitsUser, Long> {

    @PrePersist
    private void preCreate() {
        this.setCreatedDate(LocalDateTime.now());

        GoodHabitsUser current = getCurrentUser();
        if (current != null) {
            this.setCreatedBy(current);
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.setLastModifiedDate(LocalDateTime.now());
        GoodHabitsUser current = getCurrentUser();
        if (current != null) {
            this.setLastModifiedBy(current);
        }

    }

    private GoodHabitsUser getCurrentUser() {
        return getGoodHabitsUser();
    }

    public void setId(Long Id) {
        super.setId(Id);
    }

}
