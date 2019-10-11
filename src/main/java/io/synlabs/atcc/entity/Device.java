package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Device extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 64)
    private String model;

    @Column(length = 50)
    private String license;

    @Column(length = 20)
    private String status;

    @Column(length = 50)
    private String registeredTo;

    @Column(length = 50)
    private String activeConfig;
}
