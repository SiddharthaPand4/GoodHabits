package io.synlabs.synvision.views.parking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingDashboardResponse {

    private int totalSlots;
    private int freeSlots;
    private int parkedSlots;
    private int parkedMisalignedSlots;

    private int carSlots;
    private int bikeSlots;
    private int carsParked;
    private int bikesParked;

    //TODO entry grouped by hour
}
