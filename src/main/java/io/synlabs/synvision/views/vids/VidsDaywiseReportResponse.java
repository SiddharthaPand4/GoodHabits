package io.synlabs.synvision.views.vids;


import io.synlabs.synvision.enums.HighwayIncidentType;
import lombok.Data;

@Data
public class VidsDaywiseReportResponse {

   private String incidentDate;
   private HighwayIncidentType incidentType;
   private Long incidentCount;

    public VidsDaywiseReportResponse(String incidentDate, HighwayIncidentType incidentType, Long incidentCount) {
        this.incidentDate = incidentDate;
        this.incidentType = incidentType;
        this.incidentCount = incidentCount;
    }
}
