package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnprFilterRequest extends IncidentsFilterRequest {
    private String lpr;
}
