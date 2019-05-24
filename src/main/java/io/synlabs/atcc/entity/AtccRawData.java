package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class AtccRawData extends AbstractPersistable<Long> {

    @Temporal(TemporalType.TIME)
    private Date time;

    @Temporal(TemporalType.DATE)
    private Date date;

    private long timeStamp;

    private int lane;

    private BigDecimal speed;

    private int direction;

    private String type;

    private String feed;

    private String vid;

}
