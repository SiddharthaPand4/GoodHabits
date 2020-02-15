package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.entity.vids.HighwayTrafficState;
import io.synlabs.synvision.enums.TrafficDensity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@NoArgsConstructor
public class HighwayTrafficStateResponse {

    private String eventId;

    private Date updateDate;

    private BigDecimal averageSpeed;

    private long timeStamp;

    private TrafficDensity density;

    private String flowImage;

    private String flowVideo;

    private String source;

    public HighwayTrafficStateResponse(HighwayTrafficState trafficState) {
        this.eventId = trafficState.getEventId();
        this.updateDate = trafficState.getUpdateDate();
        this.averageSpeed = trafficState.getAverageSpeed();
        this.timeStamp = trafficState.getTimeStamp();
        this.density = trafficState.getDensity();
        this.flowImage = trafficState.getFlowImage();
        this.flowVideo = trafficState.getFlowVideo();
        this.source = trafficState.getSource();
    }
}
