package io.synlabs.synvision.entity.atcc;

import io.synlabs.synvision.entity.core.Feed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class AtccEvent extends AbstractPersistable<Long> {

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    private long timeStamp;

    private int lane;

    private BigDecimal speed;

    private int direction;

    private String type;

    @ManyToOne
    private Feed feed;

    private String eventImage;

    private String eventVideo;

}
