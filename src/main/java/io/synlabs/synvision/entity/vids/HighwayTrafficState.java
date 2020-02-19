package io.synlabs.synvision.entity.vids;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.enums.TrafficDensity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
public class HighwayTrafficState extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String eventId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    private BigDecimal averageSpeed;

    private long timeStamp;

    @Enumerated(EnumType.STRING)
    private TrafficDensity density;

    @Column(length = 50)
    private String flowImage;

    @Column(length = 50)
    private String flowVideo;

    @Column(length = 50)
    private String source;

    @ManyToOne
    private Feed feed;

}
