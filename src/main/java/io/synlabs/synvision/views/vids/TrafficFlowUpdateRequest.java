package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.entity.vids.HighwayTrafficState;
import io.synlabs.synvision.enums.TrafficDensity;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TrafficFlowUpdateRequest implements Request {

    private long timeStamp;
    private TrafficDensity density;
    private String flowImage;
    private String flowVideo;
    private String source;

    public HighwayTrafficState toEntity() {
        HighwayTrafficState traffic = new HighwayTrafficState();

        traffic.setEventId(UUID.randomUUID().toString());
        traffic.setUpdateDate(new Date(timeStamp * 1000));
        traffic.setTimeStamp(timeStamp);
        traffic.setDensity(density);
        traffic.setFlowImage(flowImage);
        traffic.setFlowVideo(flowVideo);
        return traffic;
    }
}
