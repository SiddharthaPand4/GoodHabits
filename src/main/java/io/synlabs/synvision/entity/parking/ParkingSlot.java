package io.synlabs.synvision.entity.parking;


import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ParkingSlot extends BaseEntity {

    @Column(length = 50)
    private String name;

    @ManyToOne
    private ParkingLot lot;

    //whether slot is free or filled
    private boolean free;

    //grouping based on camera or lane etc
    @Column(length = 50)
    private String slotGroup;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private boolean misaligned;

    private int x;
    private int y;

    private int p1x;
    private int p1y;

    private int p2x;
    private int p2y;

    private int p3x;
    private int p3y;

    private int p4x;
    private int p4y;

}
