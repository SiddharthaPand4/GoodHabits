package io.synlabs.synvision.views.anpr;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AnprReportRequest implements Request {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    public Date from;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    public Date to;
    public String fromDateString;
    public String fromTime;
    public String toDateString;
    public String toTime;
    private String lpr;
    public int page;
    public int pageSize;
}
