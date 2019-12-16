package io.synlabs.synvision.views.incident;

import lombok.Getter;

import java.util.Date;

@Getter
public class IncidentCountResponse {
    private String date;
    private String incidentType;
    private Long vehicleCount;

    public IncidentCountResponse(String date, String incidentType, Long vehicleCount) {
        this.date = date;
        this.incidentType = incidentType;
        this.vehicleCount = vehicleCount == null ? 0 : vehicleCount;
    }
}
