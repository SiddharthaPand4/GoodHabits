package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.common.PageResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itrs on 10/21/2019.
 */
public class IncidentRepeatPageResponse extends PageResponse {

    private List<IncidentRepeatCount> events = new ArrayList<>();

    public IncidentRepeatPageResponse(int pageSize, int pageCount, int pageNumber, List<IncidentRepeatCount> repeaters)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = repeaters;
    }

    public List<IncidentRepeatCount> getEvents() {
        return events;
    }
}
