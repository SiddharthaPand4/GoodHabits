package io.synlabs.synvision.views.hotlist;

import io.synlabs.synvision.entity.anpr.HotListVehicle;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

@Getter
public class HotListVehicleResponse implements Response {
    private Long id;
    private String lpr;
    private boolean archived;

    public HotListVehicleResponse(HotListVehicle vehicle) {
        this.id = vehicle.getId();
        this.lpr = vehicle.getLpr();
        this.archived = vehicle.isArchived();
    }

}
