package io.synlabs.synvision.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SurveyRequest implements Request {

    private Long id;
    private String name;
    private String folder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "IST")
    private Date endDate;

    public Long getId() {
        return unmask(id);
    }
}
