package io.synlabs.synvision.service.avc;

import io.synlabs.synvision.entity.avc.AvcEvent;
import io.synlabs.synvision.entity.avc.Survey;
import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.jpa.AvcEventRepository;
import io.synlabs.synvision.jpa.FeedRepository;
import io.synlabs.synvision.jpa.SurveyRepository;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.views.avc.AvcEventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AvcDataService extends BaseService {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private AvcEventRepository avcEventRepository;

    public void addEvent(AvcEventRequest request) {
        AvcEvent avcEvent = request.toEntity();
        Optional<Survey> opSurvey = surveyRepository.findById(request.getSurveyId());
        if (opSurvey.isPresent()) {
            Survey survey = opSurvey.get();
            if (checkValidDuration(survey)) {
                Feed feed = feedRepository.findOneByName(request.getSource());
                avcEvent.setFeed(feed);
                avcEventRepository.save(avcEvent);
            }
        } else {
            throw new NotFoundException("Couldn't find Survey with id(unmasked) : " + request.getSurveyId());
        }
    }

    boolean checkValidDuration(Survey survey) {
        Date currentDate = new Date();
        return currentDate.after(survey.getStartDate()) && currentDate.before(survey.getEndDate());
    }

    public boolean checkValidFolderTag(String tag) {
        Survey survey = surveyRepository.findFirstByFolderName(tag);
        if (survey == null) return false;
        return checkValidDuration(survey);
    }
}
