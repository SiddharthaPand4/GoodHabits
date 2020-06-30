package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VidsFilterRequest extends IncidentsFilterRequest {
    private String incidentType;
    private String location;
}
