package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.Anpr;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.AnprRepository;
import io.synlabs.synvision.views.anpr.AnprPageResponse;
import io.synlabs.synvision.views.anpr.AnprRequest;
import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.IncidentsFilterRequest;
import io.synlabs.synvision.views.common.PageResponse;
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
    private AnprRepository anprRepository;

    private static final Logger logger = LoggerFactory.getLogger(AnprService.class);

    public PageResponse<AnprResponse> list(IncidentsFilterRequest request) {
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize());
        if (request.getFromDate() == null && request.getFromTime() == null && request.getToDate() == null && request.getToTime() == null) {

            int count = anprRepository.countAllByOrg(getAtccUser().getOrg());
            List<Anpr> anprList = anprRepository.findAllByOrg(getAtccUser().getOrg(), paging);
            List<AnprResponse> list = anprList.stream().map(AnprResponse::new).collect(Collectors.toList());
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

            int count = anprRepository.countAllByOrgAndEventDateBetween(getAtccUser().getOrg(), eventStartDate, eventEndDate);
            List<Anpr> anprList = anprRepository.findAllByOrgAndEventDateBetween(getAtccUser().getOrg(), eventStartDate, eventEndDate, paging);
            List<AnprResponse> list = anprList.stream().map(AnprResponse::new).collect(Collectors.toList());
            int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
            return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);


        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }

    public void archiveAnpr(AnprRequest request) {
        Anpr anpr = anprRepository.getOne(request.getId());
        if (anpr == null) {
            throw new NotFoundException("Cannot locate incident");
        }
        anprRepository.delete(anpr);
    }

}
