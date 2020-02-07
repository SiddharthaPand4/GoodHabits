package io.synlabs.synvision.views.apc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApcDashboardResponse {
    private String date;

    private Long peopleCount;

    public ApcDashboardResponse(String date, Long peopleCount) {
        this.date = date;
        this.peopleCount = peopleCount == null ? 0 : peopleCount;
    }
}
