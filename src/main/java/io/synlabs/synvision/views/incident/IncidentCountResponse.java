package io.synlabs.synvision.views.incident;

import lombok.Getter;

import java.util.Date;

@Getter
public class IncidentCountResponse {
    private Date date;
    private String incidentType;
    private Long vehicleCount;

    public IncidentCountResponse(Date date, String incidentType, Long vehicleCount) {
        this.date = date;
        this.incidentType = incidentType;
        this.vehicleCount = vehicleCount == null ? 0 : vehicleCount;
    }
}
