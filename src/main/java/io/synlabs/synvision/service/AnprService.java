package io.synlabs.synvision.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.anpr.HotListVehicle;
import io.synlabs.synvision.entity.anpr.QAnprEvent;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.jpa.HotListVehicleRepository;
import io.synlabs.synvision.views.anpr.*;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Created by itrs on 10/21/2019.
 */
@Service
public class AnprService extends BaseService {

    @Autowired
    private AnprEventRepository anprEventRepository;

    @Autowired
    private HotListVehicleRepository hotListVehicleRepository;

    private static final Logger logger = LoggerFactory.getLogger(AnprService.class);

    public PageResponse<AnprResponse> list(AnprFilterRequest request) {
        BooleanExpression query = getQuery(request);
        int count = (int)anprEventRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "eventDate"));

        Page<AnprEvent> page = anprEventRepository.findAll(query, paging);
        List<AnprResponse> list = page.get().map(AnprResponse::new).collect(Collectors.toList());

        return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public BooleanExpression getQuery(AnprFilterRequest request) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();
            QAnprEvent root = QAnprEvent.anprEvent;
            BooleanExpression query = root.archived.isFalse();

            if (request.getLpr() != null) {
                query = query.and(root.anprText.likeIgnoreCase("%" + request.getLpr() + "%"));
            }

            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.and(root.eventDate.after(startingDate));
            }

            if (request.getToTime() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.and(root.eventDate.after(endingDate));
            }
            return query;
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
        anprEvent.setHotlisted(checkHotListed(anprEvent));
        anprEventRepository.save(anprEvent);
    }

    private boolean checkHotListed(AnprEvent anprEvent) {
        HotListVehicle hottie = hotListVehicleRepository.findOneByLpr(anprEvent.getAnprText());
        return hottie != null;
    }

    public PageResponse<AnprResponse> listIncidents(AnprFilterRequest request) {
        BooleanExpression query = getIncidentQuery(request);
        int count = (int)anprEventRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "eventDate"));

        Page<AnprEvent> page = anprEventRepository.findAll(query, paging);
        List<AnprResponse> list = page.get().map(AnprResponse::new).collect(Collectors.toList());

        return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    private BooleanExpression getIncidentQuery(AnprFilterRequest request) {
        BooleanExpression query = getQuery(request);
        QAnprEvent root = QAnprEvent.anprEvent;
        query = query.and(root.direction.eq("rev")).or(root.helmetMissing.isTrue());
        return query;
    }
}
