package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@Getter
@Setter
@NoArgsConstructor
public class VidsResponse implements Response {

    private Long id;

    private String incidentDate;

    private long timeStamp;

    private int lane;

    private BigDecimal speed;

    private int direction;

    private String type;

    private String feed;

    private String incidentImage;
    private String incidentVideo;

    public VidsResponse(HighwayIncident incident) {
        this.id = incident.getId();
        this.incidentDate = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss").format(incident.getIncidentDate());
        this.timeStamp = incident.getTimeStamp();
        this.lane = incident.getLane();
        this.speed = incident.getSpeed();
        this.direction = incident.getDirection();
        this.type = incident.getType();
        this.feed = incident.getFeed();
        this.incidentImage = incident.getIncidentImage();
        this.incidentVideo = incident.getIncidentVideo();
    }
}
