package io.synlabs.synvision.views.frs;

import io.synlabs.synvision.views.common.PageResponse;

import java.util.List;

public class FrsEventPageResponse extends PageResponse<FrsEventResponse> {

    private List<FrsEventResponse> events;

    public FrsEventPageResponse(int pageSize, int pageCount, int pageNumber, List<FrsEventResponse> events)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = events;
    }

    public List<FrsEventResponse> getEvents() {
        return events;
    }
}
