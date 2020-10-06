package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.vids.Survey;
import io.synlabs.synvision.jpa.SurveyRepository;
import io.synlabs.synvision.views.SurveyRequest;
import io.synlabs.synvision.views.SurveyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyService.class);

    @Autowired
    private SurveyRepository surveyRepository;

    public SurveyResponse createSurvey(SurveyRequest surveyRequest) {
        return new SurveyResponse(surveyRepository.saveAndFlush(new Survey(surveyRequest)));
    }

}
