package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.common.PageResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itrs on 10/21/2019.
 */
public class AnprPageResponse extends PageResponse<AnprResponse> {

    private List<AnprResponse> events;

    public AnprPageResponse(int pageSize,int pageCount, int pageNumber, List<AnprResponse> anpr)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = anpr;
    }

    public List<AnprResponse> getEvents() {
        return events;
    }
}
