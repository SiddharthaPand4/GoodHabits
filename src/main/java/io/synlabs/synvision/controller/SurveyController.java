package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.SurveyService;
import io.synlabs.synvision.views.SurveyRequest;
import io.synlabs.synvision.views.SurveyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/")
    public SurveyResponse createSurvey(@RequestBody SurveyRequest surveyRequest) {
        return surveyService.createSurvey(surveyRequest);
    }

}
