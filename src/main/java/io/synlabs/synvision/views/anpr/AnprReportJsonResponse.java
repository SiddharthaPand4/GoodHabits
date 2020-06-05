package io.synlabs.synvision.views.anpr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnprReportJsonResponse {
    public String date;
    public Long totalEvents;


    public AnprReportJsonResponse(Long events ,String date) {
        this.totalEvents = events == null ? 0 : events;
        this.date=date;
    }
}
