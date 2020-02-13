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
    private String location;
    private String incidentType;
    private String incidentImage;
    private String incidentVideo;

    public VidsResponse(HighwayIncident incident) {
        this.id = incident.getId();
        this.incidentDate = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss").format(incident.getIncidentDate());
        this.timeStamp = incident.getTimeStamp();
        this.incidentType = incident.getIncidentType().name();
        this.location = incident.getFeed().getLocation();
        this.incidentImage = incident.getIncidentImage();
        this.incidentVideo = incident.getIncidentVideo();
    }
}
