package io.synlabs.synvision.views.parking;

import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingEventDashboardResponse implements Response {

    private long checkedInBikes;
    private long checkedInCars;
}
