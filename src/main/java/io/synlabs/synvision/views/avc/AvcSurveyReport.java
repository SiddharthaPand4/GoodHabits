package io.synlabs.synvision.views.avc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Tuple;

@Getter
@Setter
@NoArgsConstructor
public class AvcSurveyReport {
    private String type;
    private Integer count;

    public AvcSurveyReport(Tuple tuple) {
        this.type = tuple.get(0, String.class);
        this.count = tuple.get(1, Integer.class);
    }
}
