package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.enums.HighwayIncidentType;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateIncidentRequest implements Request {

    private long timeStamp;
    private HighwayIncidentType incidentType;
    private String incidentImage;
    private String incidentVideo;
    private Float speed;
    private String source;

    public HighwayIncident toEntity() {
        HighwayIncident incident = new HighwayIncident();

        incident.setEventId(UUID.randomUUID().toString());
        incident.setIncidentDate(new Date(timeStamp * 1000));
        incident.setTimeStamp(timeStamp);
        incident.setIncidentType(incidentType);
        incident.setIncidentImage(incidentImage);
        incident.setIncidentVideo(incidentVideo);
        return incident;
    }
}
