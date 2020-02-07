package io.synlabs.synvision.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.apc.QApcEvent;
import io.synlabs.synvision.jpa.ApcEventRepository;
import io.synlabs.synvision.views.apc.ApcDashboardRequest;
import io.synlabs.synvision.views.apc.ApcDashboardResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ApcDashboardService extends BaseService {

    @Autowired
    private ApcEventRepository apcEventRepository;
    @Autowired
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ApcDashboardService.class);

    public  List<ApcDashboardResponse> getApcPeopleCount(ApcDashboardRequest request) {
        List<ApcDashboardResponse> response = new ArrayList<>();
        QApcEvent apcEvent = QApcEvent.apcEvent;
        JPAQuery<Tuple> query= new JPAQuery<>(entityManager);
        List<Tuple> result = null;
        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }

        switch (xAxis) {
            case "Hourly":
                result = query.select(apcEvent.eventDate,apcEvent.count())
                        .from(apcEvent)
                        .where(apcEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(apcEvent.eventDate.hour()).orderBy(apcEvent.eventDate.hour().asc())
                        .fetch();

                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    Date date = tuple.get(0, Date.class);
                    calendar.setTime(date);
                    ApcDashboardResponse apcDashboardResponse = new ApcDashboardResponse(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), tuple.get(1, Long.class));
                    response.add(apcDashboardResponse);
                    result.set(i, null);
                }
                break;

            case "Daily":
            default:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                result = query
                        .select(
                                apcEvent.eventDate,
                                apcEvent.count())
                        .from(apcEvent)
                        .where(apcEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(apcEvent.eventDate.dayOfMonth(), apcEvent.eventDate.month(), apcEvent.eventDate.year()).orderBy(apcEvent.eventDate.asc())
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    ApcDashboardResponse apcDashboardResponse = new ApcDashboardResponse(formatter.format(tuple.get(0, Date.class)), tuple.get(1, Long.class));

                    response.add(apcDashboardResponse);
                    result.set(i, null);
                }
                break;
        }




        return response;
    }
}
