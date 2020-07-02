package io.synlabs.synvision.views.atcc;

import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.common.PageResponse;

import java.util.List;

public class AtccPageResponse extends PageResponse<AtccRawDataResponse> {
    private List<AtccRawDataResponse> events;

    public AtccPageResponse(int pageSize,int pageCount, int pageNumber, List<AtccRawDataResponse> atcc)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = atcc;
    }
    public List<AtccRawDataResponse> getEvents() {
        return events;
    }
}
