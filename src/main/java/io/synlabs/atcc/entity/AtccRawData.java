package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Entity
public class AtccRawData extends AbstractPersistable<Long> {

    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ts;

    private int lane;

}
