package io.synlabs.synvision.views.incident;

import io.synlabs.synvision.enums.HighwayIncidentType;
import lombok.Getter;

import java.util.Date;

@Getter
public class IncidentCountResponse {
    private String date;
    private String incidentType;
    private Long vehicleCount;
    private Long incidentCount;

    public IncidentCountResponse(String date, String incidentType, Long vehicleCount) {
        this.date = date;
        this.incidentType = incidentType;
        this.vehicleCount = vehicleCount == null ? 0 : vehicleCount;
    }

    public IncidentCountResponse(String date, HighwayIncidentType incidentType, Long incidentCount) {
        this.date = date;
        this.incidentType = incidentType.name();
        this.incidentCount = incidentCount == null ? 0 : incidentCount;
    }
}
