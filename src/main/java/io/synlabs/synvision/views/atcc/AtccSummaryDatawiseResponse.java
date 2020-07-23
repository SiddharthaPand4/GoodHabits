package io.synlabs.synvision.views.atcc;

import lombok.Data;

@Data
public class AtccSummaryDatawiseResponse {
    private String eventDate;
    private String vehicleClass;
    private Long eventCount;


    public AtccSummaryDatawiseResponse(String eventDate, String vehicleClass, Long eventCount) {
        this.eventDate = eventDate;
        this.vehicleClass = vehicleClass;
        this.eventCount = eventCount;
    }
}
