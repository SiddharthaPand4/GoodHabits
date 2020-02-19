package io.synlabs.synvision.views.anpr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnprReportResponse {
    public String date;
    public Long totalEvents;


    public AnprReportResponse(Long events ,String date) {
        this.totalEvents = events == null ? 0 : events;
        this.date=date;
    }
}
