package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.views.common.FeedResponse;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class VidsResponse implements Response {

    private Long id;
    private Date incidentDate;
    private long timeStamp;
    private String location;
    private String incidentType;
    private String incidentImage;
    private String incidentVideo;
    private FeedResponse feed;

    public VidsResponse(HighwayIncident incident) {
        this.id = incident.getId();
        this.incidentDate = incident.getIncidentDate();
        this.timeStamp = incident.getTimeStamp();
        this.incidentType = incident.getIncidentType().name();
        this.location = incident.getFeed().getLocation();
        this.incidentImage = incident.getIncidentImage();
        this.incidentVideo = incident.getIncidentVideo();

        if (incident.getFeed() != null) {
            this.feed = new FeedResponse(incident.getFeed());
        }

    }
}
