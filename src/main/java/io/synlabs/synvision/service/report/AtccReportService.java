package io.synlabs.synvision.service.report;

import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQuery;

import io.synlabs.synvision.entity.atcc.AtccEvent;
import io.synlabs.synvision.entity.atcc.QAtccEvent;
import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.entity.vids.QHighwayIncident;
import io.synlabs.synvision.enums.HighwayIncidentType;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.util.DateUtil;
import io.synlabs.synvision.views.anpr.AnprReportJsonResponse;
import io.synlabs.synvision.views.anpr.AnprReportRequest;
import io.synlabs.synvision.views.anpr.AnprReportResponse;
import io.synlabs.synvision.views.atcc.AtccReportRequest;
import io.synlabs.synvision.views.atcc.AtccSummaryDatawiseResponse;
import io.synlabs.synvision.views.vids.VidsDaywiseReportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AtccReportService extends BaseService {

    private final EntityManager entityManager;

    @Value("${file.upload-dir}")
    private String uploadDirPath;

    private static final Logger logger = LoggerFactory.getLogger(AtccReportService.class);

    @Autowired
    public AtccReportService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String downloadAtccEvents(AtccReportRequest request) throws IOException {

        String pattern = "yyyy-MM-dd HH:mm:ss";
        request.setFrom(DateUtil.parseDateString(request.getFromDateString(), pattern));
        request.setTo(DateUtil.parseDateString(request.getToDateString(), pattern));

        QAtccEvent atccEvent = new QAtccEvent("atccEvent");
        JPAQuery<AtccEvent> query = new JPAQuery<>(entityManager);
        List<AtccEvent> result = null;
        int page = 1;
        int offset = 0;
        int limit = 1000;
        query.select(atccEvent)
                .from(atccEvent)
                .where(atccEvent.eventDate.between(request.getFrom(), request.getTo()));


        if (request.getFeedId() != null && request.getFeedId() != 0) {
            query.where(atccEvent.feed.id.eq(request.getFeedId()));
        }
        query.orderBy(atccEvent.eventDate.asc());

        long totalRecordsCount = query.fetchCount();
        Path path = Paths.get(uploadDirPath);
        String filename = null;
        FileWriter fileWriter = null;

        filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
        fileWriter = new FileWriter(filename);

        fileWriter.append("Sr. No");
        fileWriter.append(',');
        fileWriter.append("Event Date");
        fileWriter.append(',');
        fileWriter.append("Event Time");
        fileWriter.append(',');
        fileWriter.append("Event ID");
        fileWriter.append(',');
        fileWriter.append("Vehicle Class");
        fileWriter.append(',');
        fileWriter.append("Speed");
        fileWriter.append(',');
        fileWriter.append("Lane");
        fileWriter.append(',');
        fileWriter.append("Direction");
        fileWriter.append(',');
        fileWriter.append("Location");
        fileWriter.append(',');
        fileWriter.append("Site");
        fileWriter.append('\n');
        while (totalRecordsCount > offset) {
            offset = (page - 1) * limit;
            if (offset > 0) {
                query.offset(offset);
            }
            query.limit(limit);
            result = query.fetch();

            int i = 0;
            for (AtccEvent event : result) {
                fileWriter.append(String.valueOf('"')).append(String.valueOf(i + 1)).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getEventDate(), "dd-MM-yyyy")).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getEventDate(), "HH:mm:ss")).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(event.getEventId()).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(event.getType()).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(event.getSpeed().toPlainString()).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(String.valueOf(event.getLane())).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(String.valueOf(event.getDirection())).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(event.getFeed() != null ? event.getFeed().getLocation() : "").append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(event.getFeed() != null ? event.getFeed().getLocation() : "").append(String.valueOf('"'));
                fileWriter.append('\n');
                i++;
            }
            page++;
        }

        fileWriter.flush();
        fileWriter.close();
        return filename;
    }

    public String downloadEventsDaywiseSummary(AtccReportRequest request) throws IOException {
        List<String> vehicleTypes = getDistinctVehicleTypes();

        String pattern = "yyyy-MM-dd HH:mm:ss";
        request.setFrom(DateUtil.parseDateString(request.getFromDateString(), pattern));
        request.setTo(DateUtil.parseDateString(request.getToDateString(), pattern));

        QAtccEvent atccEvent = new QAtccEvent("atccEvent");
        JPAQuery<com.querydsl.core.Tuple> query = new JPAQuery<>(entityManager);

        Date eventDate = null;
        String eventDateString;
        Long eventCount = null;
        String vehicleType = null;

        List<com.querydsl.core.Tuple> result = null;
        Map<Date, List<AtccSummaryDatawiseResponse>> totalEventsByDate = new TreeMap<>();
        AtccSummaryDatawiseResponse response;
        List<AtccSummaryDatawiseResponse> responses;

        query
                .select(atccEvent.eventDate, //0
                        atccEvent.type, //1
                        atccEvent.count() //2
                )
                .from(atccEvent)
                .where(atccEvent.eventDate.between(request.getFrom(), request.getTo()));

        if (request.getFeedId() != null && request.getFeedId() != 0) {
            query.where(atccEvent.feed.id.eq(request.getFeedId()));
        }
        result = query.groupBy(atccEvent.eventDate.dayOfMonth(), atccEvent.eventDate.month(), atccEvent.eventDate.year(), atccEvent.type)
                .orderBy(atccEvent.eventDate.asc())
                .fetch();

        com.querydsl.core.Tuple tuple;
        for (int i = 0; i < result.size(); i++) {
            tuple = result.get(i);

            eventDate = tuple.get(0, Date.class);
            vehicleType = tuple.get(1, String.class);
            eventCount = tuple.get(2, Long.class);

            eventDateString = toFormattedDate(eventDate, "dd/MM/yyyy");
            eventDate = DateUtil.parseDateString(eventDateString, "dd/MM/yyyy");

            response = new AtccSummaryDatawiseResponse(eventDateString, vehicleType, eventCount);
            responses = totalEventsByDate.get(eventDate);
            if (responses == null) {
                responses = new ArrayList<>();
            }
            responses.add(response);
            totalEventsByDate.put(eventDate, responses);
            result.set(i, null);
        }

        Path path = Paths.get(uploadDirPath);
        String filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.append("Sr. No");
        fileWriter.append(',');
        fileWriter.append("Date");
        fileWriter.append(',');
        for (String vehicleType1 : vehicleTypes) {
            fileWriter.append(vehicleType1);
            fileWriter.append(',');
        }
        fileWriter.append("Total Incidents");
        fileWriter.append('\n');

        int i = 0;

        for (Date key : totalEventsByDate.keySet()) {
            fileWriter.append(String.valueOf('"')).append(String.valueOf(i + 1)).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(toFormattedDate(key, "dd/MM/yyyy")).append(String.valueOf('"'));
            fileWriter.append(',');
            for (String vehicleType1 : vehicleTypes) {
                int count = 0;
                for (AtccSummaryDatawiseResponse res : totalEventsByDate.get(key)) {
                    if (vehicleType1.equals(res.getVehicleClass())) {
                        count = res.getEventCount().intValue();
                        break;
                    }
                }
                fileWriter.append(String.valueOf('"')).append(String.valueOf(count)).append(String.valueOf('"'));
                fileWriter.append(',');
            }
            long totalEvents = 0;
            for (AtccSummaryDatawiseResponse res : totalEventsByDate.get(key)) {
                totalEvents = totalEvents + res.getEventCount();
            }
            fileWriter.append(String.valueOf('"')).append(String.valueOf(totalEvents)).append(String.valueOf('"'));
            fileWriter.append('\n');
            i++;
        }

        fileWriter.flush();
        fileWriter.close();
        return filename;
    }

    private List<String> getDistinctVehicleTypes() {
        QAtccEvent atccEvent = new QAtccEvent("atccEvent");
        return new JPAQuery<>(entityManager)
                .select(atccEvent.type)
                .distinct()
                .from(atccEvent)
                .fetch();
    }
}
