package io.synlabs.synvision.views.parking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by itrs on 02/03/2020.
 */
@Getter
@Setter
public class ParkingReportRequest {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    public Date from;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    public Date to;
    public String filterType;
    public String xAxis;
    public String reportType;

    public String fromDateString;
    public String toDateString;
}
