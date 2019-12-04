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
}
