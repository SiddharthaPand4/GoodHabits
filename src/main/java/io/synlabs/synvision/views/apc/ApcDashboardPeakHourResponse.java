package io.synlabs.synvision.views.apc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApcDashboardPeakHourResponse {
    private String duration;
    private Double peopleCount;

    public ApcDashboardPeakHourResponse(String duration, Double peopleCount) {
        this.duration = duration;
        this.peopleCount = peopleCount;
    }

}
