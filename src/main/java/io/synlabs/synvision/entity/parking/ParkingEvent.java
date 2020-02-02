package io.synlabs.synvision.entity.parking;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ParkingEvent extends BaseEntity {

    @Column(length = 50)
    private String vehicleNo;

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date checkIn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date checkOut;

    private boolean archived;

    @Column(length = 50)
    private String source;

    @Enumerated(EnumType.STRING)
    private VehicleType type;
}
