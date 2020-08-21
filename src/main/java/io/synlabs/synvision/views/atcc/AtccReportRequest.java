package io.synlabs.synvision.views.atcc;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AtccReportRequest implements Request {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    private Date from;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "IST")
    private Date to;
    private String fromDateString;
    private String fromTime;
    private String toDateString;
    private String toTime;
    private int page;
    private int pageSize;

    private String reportType;
    private String reportFileType;

    private Long feedId;

    public Long getFeedId() {
        return unmask(feedId);
    }
}
