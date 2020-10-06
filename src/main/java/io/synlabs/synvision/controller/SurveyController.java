package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.SurveyService;
import io.synlabs.synvision.views.SurveyRequest;
import io.synlabs.synvision.views.SurveyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping
    public SurveyResponse createSurvey(@RequestBody SurveyRequest surveyRequest) {
        return surveyService.createSurvey(surveyRequest);
    }

    @DeleteMapping
    public void deleteSurvey(@RequestParam Long surveyId) {
        surveyService.deleteSurvey(surveyId);
    }

    @GetMapping("/duplicates")
    public boolean checkDuplicates(@RequestParam String surveyFolder) {
        return surveyService.checkDuplicateSurvey(surveyFolder);
    }

    @GetMapping("/list")
    public List<SurveyResponse> surveyList() {
        return surveyService.surveyList();
    }

}
