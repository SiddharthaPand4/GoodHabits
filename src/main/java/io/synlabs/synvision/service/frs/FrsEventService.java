package io.synlabs.synvision.service.frs;

import com.querydsl.core.types.dsl.BooleanExpression;
import io.synlabs.synvision.entity.frs.FrsEvent;
import io.synlabs.synvision.entity.frs.QFrsEvent;
import io.synlabs.synvision.jpa.FrsEventRepository;
import io.synlabs.synvision.views.frs.FrsEventPageResponse;
import io.synlabs.synvision.views.frs.FrsEventResponse;
import io.synlabs.synvision.views.frs.FrsFilterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class FrsEventService {

    private static final Logger logger = LoggerFactory.getLogger(FrsEventService.class);

    @Autowired
    private FrsEventRepository eventRepository;


    public FrsEventPageResponse getEvents(FrsFilterRequest request) {
        BooleanExpression query = getQuery(request);
        int count = (int) eventRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "eventDate"));
        Page<FrsEvent> page = eventRepository.findAll(query, paging);
        List<FrsEventResponse> list = new ArrayList<>(page.getSize());
        page.get().forEach(item -> {
            list.add(new FrsEventResponse(item));
        });

        return new FrsEventPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }
    public BooleanExpression getQuery(FrsFilterRequest request) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();
            QFrsEvent root = new QFrsEvent("frsEvent");
            BooleanExpression query = root.archived.isFalse();

            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.and(root.eventDate.after(startingDate));
            }

            if (request.getToDate() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.and(root.eventDate.before(endingDate));
            }
            return query;
        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }

}