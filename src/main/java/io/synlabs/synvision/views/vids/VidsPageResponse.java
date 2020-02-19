package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.common.PageResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VidsPageResponse extends PageResponse<VidsResponse> {

    private List<VidsResponse> events;

    public VidsPageResponse(int pageSize, int pageCount, int pageNumber, List<VidsResponse> incidents)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = incidents;
    }

    public List<VidsResponse> getEvents() {
        return events;
    }
}
