package io.synlabs.synvision.views.parking;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParkingEventCountResponse {

    private List<VehicleCountResponse> checkInEvents = new ArrayList<>();
    private List<VehicleCountResponse> checkOutEvents = new ArrayList<>();
}
