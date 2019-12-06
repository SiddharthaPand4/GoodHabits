package io.synlabs.synvision.entity.anpr;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by itrs on 10/21/2019.
 */
@Entity
@Getter
@Setter
public class AnprEvent extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @Column(length = 50)
    private String vehicleImage;

    @Column(length = 50)
    private String ocrImage;

    @Column(length = 50)
    private String anprText;

    private boolean archived;

    @Column(length = 50)
    private String direction;

    @Column(length = 50)
    private String vehicleClass;

    private boolean helmetMissing;

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setHelmetMissing(boolean helmetMissing) {
        this.helmetMissing = helmetMissing;
    }

    public boolean getHelmetMissing() {
        return helmetMissing;
    }
}
