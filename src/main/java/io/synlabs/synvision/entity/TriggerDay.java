package io.synlabs.synvision.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class TriggerDay extends BaseEntity {

    private int day;
    private int start;
    private int End;

    @ManyToOne
    private Trigger trigger;

}
