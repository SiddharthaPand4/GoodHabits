package io.synlabs.synvision.service;

import com.querydsl.core.types.dsl.BooleanExpression;

import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.entity.vids.QHighwayIncident;
import io.synlabs.synvision.jpa.HighwayIncidentRepository;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.vids.VidsFilterRequest;
import io.synlabs.synvision.views.vids.VidsPageResponse;
import io.synlabs.synvision.views.vids.VidsResponse;
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
public class VidsService {

    private static final Logger logger = LoggerFactory.getLogger(VidsService.class);

    @Autowired
    private HighwayIncidentRepository incidentRepository;

    public PageResponse<VidsResponse> listIncidents(VidsFilterRequest request) {
        BooleanExpression query = getQuery(request);
        int count = (int) incidentRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "incidentDate"));

        Page<HighwayIncident> page = incidentRepository.findAll(query, paging);
        //List<AnprResponse> list = page.get().map(AnprResponse::new).collect(Collectors.toList());

        List<VidsResponse> list = new ArrayList<>(page.getSize());
        page.get().forEach(item -> {
            list.add(new VidsResponse(item));
        });

        return (PageResponse<VidsResponse>) new VidsPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public BooleanExpression getQuery(VidsFilterRequest request) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();
            QHighwayIncident root = QHighwayIncident.highwayIncident;
            BooleanExpression query = root.archived.isFalse();

            //TODO add time also
            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.and(root.incidentDate.after(startingDate));
            }

            if (request.getToDate() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.and(root.incidentDate.before(endingDate));
            }
            return query;
        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }


}
