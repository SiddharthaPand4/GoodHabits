package io.synlabs.synvision.views.atcc;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AtccVehicleCountResponse {

    private Date date;
    private String vehicleType;
    private Long vehicleCount;

    public AtccVehicleCountResponse(Date date, String vehicleType, Long vehicleCount) {
        this.date = date;
        this.vehicleType = vehicleType;
        this.vehicleCount = vehicleCount == null ? 0 : vehicleCount;
    }
}
