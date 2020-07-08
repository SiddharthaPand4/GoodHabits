package io.synlabs.synvision.views.atcc;

import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.common.PageResponse;
import lombok.Getter;

import java.util.List;
@Getter
public class AtccPageResponse extends PageResponse<AtccRawDataResponse> {
    private List<AtccRawDataResponse> events;

    public AtccPageResponse(int pageSize,int pageCount, int pageNumber, List<AtccRawDataResponse> atcc)
    {
        super(pageSize, pageCount, pageNumber);
        this.events = atcc;
    }

}
