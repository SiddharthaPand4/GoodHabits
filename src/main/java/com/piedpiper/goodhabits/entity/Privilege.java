package com.piedpiper.goodhabits.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Privilege extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    public Privilege(Privilege p)
    {
        this.name = p.name;
    }

}
