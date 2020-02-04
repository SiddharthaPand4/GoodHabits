package io.synlabs.synvision.views.parking;

import lombok.Getter;

@Getter
public class VehicleCountResponse {
    private String date;
    private String eventType;
    private Long vehicleCount;

    public VehicleCountResponse(String date, String eventType, Long vehicleCount) {
        this.date = date;
        this.eventType = eventType;
        this.vehicleCount = vehicleCount == null ? 0 : vehicleCount;
    }
}
