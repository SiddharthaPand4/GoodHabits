package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Setter
@Getter
public class AtccVideoData extends AbstractPersistable<Long> {

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Temporal(TemporalType.DATE)
    private Date date;

    private long timeStamp;

}
