package io.synlabs.synvision.service.avc;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.avc.Survey;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.jpa.SurveyRepository;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.views.avc.SurveyRequest;
import io.synlabs.synvision.views.avc.SurveyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyService.class);

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    public SurveyResponse createSurvey(SurveyRequest surveyRequest) {
        if (!Paths.get(fileStorageProperties.getUploadDir(), surveyRequest.getFolder()).toFile().mkdirs()) {
            throw new FileStorageException("Can't create folder for given survey");
        }
        return new SurveyResponse(surveyRepository.saveAndFlush(new Survey(surveyRequest)));
    }

    public boolean checkDuplicateSurvey(String surveyFolder) {
        return surveyRepository.findFirstByFolderName(surveyFolder) != null;
    }

    public void deleteSurvey(Long surveyId) {
        surveyRepository.deleteById(surveyId);
    }

    public List<SurveyResponse> surveyList() {
        return surveyRepository.findAll().stream().map(SurveyResponse::new).collect(Collectors.toList());
    }
}
