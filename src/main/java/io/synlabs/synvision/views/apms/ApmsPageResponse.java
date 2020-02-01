package io.synlabs.synvision.views.apms;

import io.synlabs.synvision.views.common.PageResponse;

import java.util.ArrayList;
import java.util.List;

public class ApmsPageResponse extends PageResponse{

    private List<ApmsResponse> events = new ArrayList<>();

    public ApmsPageResponse(int pageSize,int pageCount, int pageNumber, List<ApmsResponse> apms)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = apms ;
    }

    public List<ApmsResponse> getEvents() {
        return events;
    }
}
