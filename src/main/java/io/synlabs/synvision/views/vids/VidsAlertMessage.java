package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.entity.vids.HighwayIncident;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class VidsAlertMessage {

    private String eventId;

    private Date incidentDate;

    private String type;

    private long timeStamp;

    private String location;

    private String incidentImage;

    private String incidentVideo;

    private String message;

    public VidsAlertMessage(HighwayIncident event) {

        this.eventId = event.getEventId();
        this.message = "Alert!";
        this.incidentImage = event.getIncidentImage();
        this.incidentVideo = event.getIncidentVideo();
        this.incidentDate = event.getIncidentDate();
        this.timeStamp = event.getTimeStamp();
        this.location = event.getFeed() == null ? "Unkown" : event.getFeed().getName();
        this.type = event.getIncidentType().name();
    }
}
