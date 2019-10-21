package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.Incident;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.IncidentsRepository;
import io.synlabs.synvision.views.IncidentPageResponse;
import io.synlabs.synvision.views.IncidentRequest;
import io.synlabs.synvision.views.IncidentsFilterRequest;
import io.synlabs.synvision.views.IncidentsResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by itrs on 10/16/2019.
 */
@Service
public class IncidentService extends BaseService {

    @Autowired
    private IncidentsRepository incidentsRepository;

    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);

    public PageResponse<IncidentsResponse> list(IncidentsFilterRequest request){

        if(request.getFromDate()==null && request.getFromTime()==null && request.getToDate()==null && request.getToTime()==null) {
            List<Incident> incidents= incidentsRepository.findAllByOrg(getAtccUser().getOrg());

            List<IncidentsResponse> list = incidents.stream().map(IncidentsResponse::new).collect(Collectors.toList());
            int listSize=list.size();
            int pageCount = (int) Math.ceil(listSize * 1.0 / request.getPageSize());
            return (PageResponse<IncidentsResponse>) new IncidentPageResponse(request.getPageSize(),pageCount, request.getPage(), list);

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

            List<Incident> incidents= incidentsRepository.findAllByOrgAndEventStartBetween(getAtccUser().getOrg(), eventStartDate, eventEndDate);
            List<IncidentsResponse> list = incidents.stream().map(IncidentsResponse::new).collect(Collectors.toList());
            int listSize=list.size();
            int pageCount = (int) Math.ceil(listSize * 1.0 / request.getPageSize());
            return (PageResponse<IncidentsResponse>) new IncidentPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
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
