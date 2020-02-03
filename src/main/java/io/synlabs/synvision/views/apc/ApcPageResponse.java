package io.synlabs.synvision.views.apc;
import io.synlabs.synvision.views.common.PageResponse;

import java.util.List;

public class ApcPageResponse extends PageResponse{
    private List<ApcResponse> events;

    public ApcPageResponse(List<ApcResponse> apc)
    {

        this.events = apc;
    }

    public List<ApcResponse> getEvents() {
        return events;
    }
}
