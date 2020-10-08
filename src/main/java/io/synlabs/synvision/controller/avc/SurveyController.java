package io.synlabs.synvision.controller.avc;

import io.synlabs.synvision.auth.SynvisionAuth;
import io.synlabs.synvision.service.avc.SurveyService;
import io.synlabs.synvision.util.LongObfuscator;
import io.synlabs.synvision.views.avc.SurveyRequest;
import io.synlabs.synvision.views.avc.SurveyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping
    @Secured(SynvisionAuth.Privileges.AVC_SURVEY_WRITE)
    public SurveyResponse createSurvey(@RequestBody SurveyRequest surveyRequest) {
        return surveyService.createSurvey(surveyRequest);
    }

    @DeleteMapping
    @Secured(SynvisionAuth.Privileges.AVC_SURVEY_WRITE)
    public void deleteSurvey(@RequestParam Long surveyId) {
        surveyService.deleteSurvey(LongObfuscator.INSTANCE.unobfuscate(surveyId));
    }

    @GetMapping("/duplicates")
    @Secured(SynvisionAuth.Privileges.AVC_SURVEY_WRITE)
    public boolean checkDuplicates(@RequestParam String surveyFolder) {
        return surveyService.checkDuplicateSurvey(surveyFolder);
    }

    @GetMapping("/list")
    @Secured(SynvisionAuth.Privileges.AVC_SURVEY_READ)
    public List<SurveyResponse> surveyList() {
        return surveyService.surveyList();
    }

}
