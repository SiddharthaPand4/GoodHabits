package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.entity.vids.HighwayTrafficState;
import io.synlabs.synvision.views.DashboardResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class VidsDashboardResponse {

    private List<DashboardResponse> onehourstats;
    private List<DashboardResponse> todaystats;
    private HighwayTrafficStateResponse trafficState;

    public VidsDashboardResponse(List<DashboardResponse> onehourstats, List<DashboardResponse> todaystats, HighwayTrafficState trafficState) {
        this.onehourstats = onehourstats;
        this.todaystats = todaystats;
        this.trafficState = new HighwayTrafficStateResponse(trafficState);
    }
}
