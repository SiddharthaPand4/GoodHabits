package io.synlabs.synvision.views.frs;

import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrsFilterRequest extends IncidentsFilterRequest {
    private String name;
}
