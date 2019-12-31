package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

import java.util.List;

@Getter
public class IncidentRepeatCount implements Response {

    private String anprText;
    private  int repeatedTimes;

    public IncidentRepeatCount(String anprText, long repeatedTimes) {
        this.anprText = anprText;
        this.repeatedTimes = (int)repeatedTimes;
    }
}
