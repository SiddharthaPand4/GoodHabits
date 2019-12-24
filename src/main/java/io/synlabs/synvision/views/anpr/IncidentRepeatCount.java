package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.common.Response;

import java.util.List;

public class IncidentRepeatCount implements Response {

    public  String anprText;
    public  int repeatedTimes;

    public IncidentRepeatCount(String anprText, long repeatedTimes) {
        this.anprText = anprText;
        this.repeatedTimes = (int)repeatedTimes;
    }
}
