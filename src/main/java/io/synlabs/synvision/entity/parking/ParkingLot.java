package io.synlabs.synvision.entity.parking;


import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ParkingLot extends BaseEntity {

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String address;

    private int totalSlots;

    private int freeSlots;

    private int bikeSlots;
    private int carSlots;

    private int bikesParked;
    private int carsParked;

    private String lastestImage;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    //this is the main parking location
    private boolean papalot;

    @ManyToOne
    private ParkingLot parent;
}

//TODO a lot can be divided into smaller groups
//a parent is the actual lot
//then smaller groups, all are in same table
