package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Setter
@Getter
public class ImportStatus extends AbstractPersistable<Long> {

    private String filename;

    @Temporal(TemporalType.TIMESTAMP)
    private Date importDate;

    @Temporal(TemporalType.TIME)
    @Column(name = "data_from")
    private Date from;

    @Temporal(TemporalType.TIME)
    @Column(name = "data_to")
    private Date to;
}
