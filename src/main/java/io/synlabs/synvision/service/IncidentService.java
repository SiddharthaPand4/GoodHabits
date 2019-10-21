package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.Incident;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.IncidentsRepository;
import io.synlabs.synvision.views.IncidentRequest;
import io.synlabs.synvision.views.IncidentsFilterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by itrs on 10/16/2019.
 */
@Service
public class IncidentService extends BaseService {

    @Autowired
    private IncidentsRepository incidentsRepository;

    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);

    public List<Incident> list(IncidentsFilterRequest request){
        if(request.getFromDate()==null && request.getFromTime()==null && request.getToDate()==null && request.getToTime()==null) {
            return incidentsRepository.findAllByOrg(getAtccUser().getOrg());

        }

        String fromDate = request.getFromDate();
        String toDate = request.getToDate();
        String fromTime = request.getFromTime()==null ? "00:00:00": request.getFromTime();
        String toTime = request.getToTime()==null ? "00:00:00": request.getToTime();

        String eventStart = fromDate + " " + fromTime;
        String eventEnd = toDate +  " " + toTime;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(fromDate==null){
            throw new ValidationException("From date should not be null!");
        }

        if(toDate==null){
            Date date = new Date();
            eventEnd= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }

        try {
            Date eventStartDate = dateFormat.parse(eventStart);
            Date eventEndDate = dateFormat.parse(eventEnd);

            return incidentsRepository.findAllByOrgAndEventStartBetween(getAtccUser().getOrg(), eventStartDate, eventEndDate);
        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }

    public void archiveIncident(IncidentRequest request) {
        Incident incident=incidentsRepository.getOne(request.getId());
        if(incident==null){
            throw new NotFoundException("Cannot locate incident");
        }
        incidentsRepository.delete(incident);
    }
}
