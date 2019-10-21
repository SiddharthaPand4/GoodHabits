package io.synlabs.synvision.views;

import io.synlabs.synvision.views.common.PageResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itrs on 10/21/2019.
 */
public class IncidentPageResponse extends PageResponse{
    private List<IncidentsResponse> incidents = new ArrayList<>();

    public IncidentPageResponse(int pageSize,int pageCount, int pageNumber, List<IncidentsResponse> incidents)
    {
        super(pageSize, pageCount, pageNumber);
        this.incidents = incidents;
    }

    public List<IncidentsResponse> getIncidents()
    {
        return incidents;
    }
}
