package io.synlabs.synvision.entity.vids;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.enums.TrafficState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class HighwayState extends BaseEntity {

    private BigDecimal averageSpeed;

    @Enumerated(EnumType.STRING)
    private TrafficState trafficState;

}
