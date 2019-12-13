package io.synlabs.synvision.views.hotlist;

import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.common.PageResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itrs on 10/21/2019.
 */
public class HotListVehiclePageResponse extends PageResponse {

    private List<HotListVehicleResponse> vehicles = new ArrayList<>();

    public HotListVehiclePageResponse(int pageSize, int pageCount, int pageNumber, List<HotListVehicleResponse> vehicles)
    {
        super(pageSize, pageCount, pageNumber);
        this.vehicles = vehicles;
    }

    public List<HotListVehicleResponse> getEvents() {
        return vehicles;
    }
}
