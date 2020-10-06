package io.synlabs.synvision.entity.avc;

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
public class AvcEvent extends AbstractPersistable<Long> {

    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    private long timeStamp;

    private int lane;

    private BigDecimal speed;

    private int direction;

    @Column(length = 10)
    private String type;

    private int seek;

    @ManyToOne
    private Feed feed;

    @Column(length = 200)
    private String eventImage;

    @Column(length = 200)
    private String eventVideo;

    @Column(length = 10)
    private String vid;
}
