package io.synlabs.synvision.views.anpr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnprReportResponse {
    public String date;
    public String vehicleClass;
    public Long totalEvents;


    public AnprReportResponse(String vehicleClass,Long events ,String date) {
        this.totalEvents = events == null ? 0 : events;
        this.date=date;
        this.vehicleClass=vehicleClass;
    }

}
