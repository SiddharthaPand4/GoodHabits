package com.piedpiper.goodhabits.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Community extends BaseEntity {

    private String name;

    private String description;

    @ManyToOne
    private GoodHabitsUser admin;

}
