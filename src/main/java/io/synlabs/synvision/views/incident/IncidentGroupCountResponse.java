package io.synlabs.synvision.views.incident;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IncidentGroupCountResponse {

    private List<IncidentCountResponse> helmetMissingIncidents = new ArrayList<>();
    private List<IncidentCountResponse> reverseDirectionIncidents = new ArrayList<>();
}
