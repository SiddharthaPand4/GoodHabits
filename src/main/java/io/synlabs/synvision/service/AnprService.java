package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.views.anpr.*;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.incident.IncidentRequest;
import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by itrs on 10/21/2019.
 */
@Service
public class AnprService extends BaseService {

    @Autowired
    private AnprEventRepository anprEventRepository;

    private static final Logger logger = LoggerFactory.getLogger(AnprService.class);

    public PageResponse<AnprResponse> list(IncidentsFilterRequest request) {
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize());
        if (request.getFromDate() == null && request.getFromTime() == null && request.getToDate() == null && request.getToTime() == null) {

            int count = anprEventRepository.countAllByArchivedFalse();
            List<AnprEvent> anprEventList = anprEventRepository.findAllByArchivedFalse(paging);
            List<AnprResponse> list = anprEventList.stream().map(AnprResponse::new).collect(Collectors.toList());
            int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
            return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);

        }

        String fromDate = request.getFromDate();
        String toDate = request.getToDate();
        String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
        String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();

        String eventStart = fromDate + " " + fromTime;
        String eventEnd = toDate + " " + toTime;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (fromDate == null) {
            throw new ValidationException("From date should not be null!");
        }

        if (toDate == null) {
            Date date = new Date();
            eventEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }

        try {
            Date eventStartDate = dateFormat.parse(eventStart);
            Date eventEndDate = dateFormat.parse(eventEnd);

            int count = anprEventRepository.countAllByEventDateBetweenAndArchivedFalse(eventStartDate, eventEndDate);
            List<AnprEvent> anprEventList = anprEventRepository.findAllByEventDateBetweenAndArchivedFalse(eventStartDate, eventEndDate, paging);
            List<AnprResponse> list = anprEventList.stream().map(AnprResponse::new).collect(Collectors.toList());
            int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
            return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);


        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }

    public void archiveAnpr(AnprRequest request) {
        AnprEvent anprEvent = anprEventRepository.getOne(request.getId());
        anprEventRepository.delete(anprEvent);
    }

    public void addAnprEvent(CreateAnprRequest request) {
        AnprEvent anprEvent = request.toEntity();
        anprEventRepository.save(anprEvent);
    }

}
