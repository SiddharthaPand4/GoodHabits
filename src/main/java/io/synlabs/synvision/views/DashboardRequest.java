package io.synlabs.synvision.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by itrs on 10/23/2019.
 */
@Getter
@Setter
public class DashboardRequest implements Request {
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

    private Long feedId;
    public Long getFeedId()
    {
        return unmask(feedId);
    }

}
