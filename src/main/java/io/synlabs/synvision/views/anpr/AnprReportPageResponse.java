package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.parking.ApmsResponse;

import java.util.ArrayList;
import java.util.List;

public class AnprReportPageResponse extends PageResponse {

    private List<AnprReportResponse> events = new ArrayList<>();

    public AnprReportPageResponse(int pageSize,int pageCount, int pageNumber, List<AnprReportResponse> anpr)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = anpr ;
    }

    public List<AnprReportResponse> getEvents() {
        return events;
    }
}
