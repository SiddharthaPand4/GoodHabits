package io.synlabs.synvision.service.report;

import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQuery;

import io.synlabs.synvision.entity.atcc.AtccEvent;
import io.synlabs.synvision.entity.atcc.QAtccEvent;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.views.anpr.AnprReportJsonResponse;
import io.synlabs.synvision.views.anpr.AnprReportRequest;
import io.synlabs.synvision.views.anpr.AnprReportResponse;
import io.synlabs.synvision.views.atcc.AtccReportRequest;
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
        int page = 1;
        int offset = 0;
        int limit = 1000;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }

        QAtccEvent atccEvent = new QAtccEvent("atccEvent");
        JPAQuery<AtccEvent> query = new JPAQuery<>(entityManager);
        List<AtccEvent> result = null;

        query.select(atccEvent)
                .from(atccEvent)
                .where(atccEvent.eventDate.between(request.getFrom(), request.getTo()))
                .orderBy(atccEvent.eventDate.asc());


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
        fileWriter.append("Lane");
        fileWriter.append(',');
        fileWriter.append("Speed");
        fileWriter.append(',');
        fileWriter.append("Vehicle Class");
        fileWriter.append(',');
        fileWriter.append("Direction");
        fileWriter.append(',');
        fileWriter.append("Location");
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

                fileWriter.append('\n');
                i++;
            }
            page++;
        }

        fileWriter.flush();
        fileWriter.close();
        return filename;
    }

    public String downloadatccEventsOnDailyBasis(AnprReportRequest request) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }

        QAtccEvent atccEvent = new QAtccEvent("atccEvent");
        JPAQuery<AtccEvent> query = new JPAQuery<>(entityManager);
        JPAQuery<AtccEvent> query1 = new JPAQuery<>(entityManager);
        JPAQuery<AtccEvent> query2 = new JPAQuery<>(entityManager);

        Date eventDate = null;
        Long eventCount = null;
        String vehicleClass = null;

        List<com.querydsl.core.Tuple> result = null;
        List<com.querydsl.core.Tuple> result1 = null;
        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();
        Map<Date, List<AnprReportResponse>> totalEventsByDate = new TreeMap<Date, List<AnprReportResponse>>();
        Map<Date, AnprReportJsonResponse> totalEventsByDateForJsonFormat = new TreeMap<Date, AnprReportJsonResponse>();

        List<String> vehicleClassList = query1.select(atccEvent.type).distinct()
                .from(atccEvent)
                .fetch();
        switch (xAxis) {

            case "DayWise Summary":

                //---FOR JSON format
                result1 = query2
                        .select(atccEvent.eventDate,
                                atccEvent.count())
                        .from(atccEvent)
                        .where(atccEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(atccEvent.eventDate.dayOfMonth(), atccEvent.eventDate.month(), atccEvent.eventDate.year())
                        .orderBy(atccEvent.eventDate.asc())
                        .fetch();


                for (int i = 0; i < result1.size(); i++) {
                    com.querydsl.core.Tuple tuple = result1.get(i);

                    eventDate = tuple.get(0, Date.class);
                    String eventDateString = toFormattedDate(eventDate, "dd/MM/yyyy");

                    try {
                        eventDate = sdf1.parse(eventDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    eventCount = tuple.get(1, Long.class);

                    totalEventsByDateForJsonFormat.put(eventDate, new AnprReportJsonResponse(eventCount, eventDateString));
                    result1.set(i, null);
                }

                //---- For CSV format, groupby with vehicleclass also
                result = query
                        .select(atccEvent.eventDate, atccEvent.type,
                                atccEvent.count())
                        .from(atccEvent)
                        .where(atccEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(atccEvent.eventDate.dayOfMonth(), atccEvent.eventDate.month(), atccEvent.eventDate.year(), atccEvent.type)
                        .orderBy(atccEvent.eventDate.asc())
                        .fetch();


                for (int i = 0; i < result.size(); i++) {
                    com.querydsl.core.Tuple tuple = result.get(i);

                    eventDate = tuple.get(0, Date.class);
                    String eventDateString = toFormattedDate(eventDate, "dd/MM/yyyy");

                    try {
                        eventDate = sdf1.parse(eventDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    vehicleClass = tuple.get(1, String.class);
                    eventCount = tuple.get(2, Long.class);

                    AnprReportResponse response = new AnprReportResponse(vehicleClass, eventCount, eventDateString);
                    List<AnprReportResponse> responses = totalEventsByDate.get(eventDate);
                    if (responses == null) {
                        responses = new ArrayList<AnprReportResponse>();
                    }

                    responses.add(response);

                    totalEventsByDate.put(eventDate, responses);
                    result.set(i, null);
                }


                break;
        }
        List<AnprReportJsonResponse> responses = new ArrayList<>();
        for (Date date : totalEventsByDateForJsonFormat.keySet()) {
            responses.add(new AnprReportJsonResponse(totalEventsByDateForJsonFormat.get(date).getTotalEvents(), toFormattedDate(date, "dd/MM/yyyy")));
        }

        Path path = Paths.get(uploadDirPath);
        String filename = null;
        FileWriter fileWriter = null;
        switch (request.getReportType()) {
            case "CSV":
                filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
                fileWriter = new FileWriter(filename);
                fileWriter.append("Sr. No");
                fileWriter.append(',');
                fileWriter.append("Date");
                fileWriter.append(',');
                for (String vehicleclass : vehicleClassList) {
                    fileWriter.append(vehicleclass);
                    fileWriter.append(',');
                }
                fileWriter.append("Total Events");
                fileWriter.append('\n');

                int i = 0;

                for (Date key : totalEventsByDate.keySet()) {
                    fileWriter.append(String.valueOf('"')).append(String.valueOf(i + 1)).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(toFormattedDate(key, "dd/MM/yyyy")).append(String.valueOf('"'));
                    fileWriter.append(',');
                    for (String vehicleclass : vehicleClassList) {
                        int count = 0;
                        for (AnprReportResponse anprReportResponse : totalEventsByDate.get(key)) {
                            if (vehicleclass.equals(anprReportResponse.getVehicleClass())) {
                                count = anprReportResponse.getTotalEvents().intValue();
                                break;
                            }
                        }
                        fileWriter.append(String.valueOf('"')).append(String.valueOf(count)).append(String.valueOf('"'));
                        fileWriter.append(',');
                    }
                    int totalEvents = 0;
                    for (AnprReportResponse anprReportResponse : totalEventsByDate.get(key)) {
                        totalEvents = totalEvents + anprReportResponse.getTotalEvents().intValue();
                    }
                    fileWriter.append(String.valueOf('"')).append(String.valueOf(totalEvents)).append(String.valueOf('"'));
                    fileWriter.append('\n');
                    i++;
                }

                break;

            case "JSON":
                filename = path.resolve(UUID.randomUUID().toString() + ".json").toString();
                fileWriter = new FileWriter(filename);

                Gson gson = new Gson();
                gson.toJson(responses, fileWriter);

                break;

        }


        fileWriter.flush();
        fileWriter.close();
        return filename;
    }

}
