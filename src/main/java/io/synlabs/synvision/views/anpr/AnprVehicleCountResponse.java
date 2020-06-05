package io.synlabs.synvision.views.anpr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnprVehicleCountResponse {
    private String date;
    private Long vehicleCount;
    private String vehicleType;

    public AnprVehicleCountResponse(String date,String vehicleType, Long vehicleCount) {
        this.date = date;
        this.vehicleCount = vehicleCount == null ? 0 : vehicleCount;
        this.vehicleType = vehicleType;
    }
}
