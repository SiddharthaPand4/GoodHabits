package io.synlabs.synvision.views;

import io.synlabs.synvision.entity.vids.Survey;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SurveyResponse implements Response {

    private Long id;
    private String name;
    private String folder;
    private Date startDate;
    private Date endDate;

    public SurveyResponse(Survey survey) {
        this.id = mask(survey.getId());
        this.name = survey.getName();
        this.folder = survey.getFolderName();
        this.startDate = survey.getStartDate();
        this.endDate = survey.getEndDate();
    }
}
