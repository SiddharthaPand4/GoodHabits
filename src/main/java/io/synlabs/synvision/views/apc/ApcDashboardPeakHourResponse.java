package io.synlabs.synvision.views.apc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApcDashboardPeakHourResponse {
    private String duration;
    private double peopleCountPercentage;
    private long peopleCount;

    public ApcDashboardPeakHourResponse(String duration, double peopleCountPercentage, long peopleCount) {
        this.duration = duration;
        this.peopleCountPercentage = peopleCountPercentage;
        this.peopleCount = peopleCount;
    }

}
