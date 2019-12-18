package io.synlabs.synvision.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by itrs on 10/23/2019.
 */
@Getter
@Setter
public class DashboardRequest {
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
