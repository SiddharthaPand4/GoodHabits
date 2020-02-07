package io.synlabs.synvision.views.apc;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ApcDashboardRequest {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    public Date from;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    public Date to;
    public int month;
    public int year;
    public Date selectedDate;

    public String filterType;
    public String xAxis;

    public String fromDateString;
    public String toDateString;

}