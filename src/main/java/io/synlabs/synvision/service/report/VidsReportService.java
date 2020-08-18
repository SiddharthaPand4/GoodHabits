package io.synlabs.synvision.service.report;

import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.entity.vids.QHighwayIncident;
import io.synlabs.synvision.enums.HighwayIncidentType;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.util.DateUtil;
import io.synlabs.synvision.views.anpr.AnprReportRequest;
import io.synlabs.synvision.views.atcc.AtccReportRequest;
import io.synlabs.synvision.views.vids.VidsDaywiseReportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VidsReportService extends BaseService {

    private final EntityManager entityManager;

    @Value("${file.upload-dir}")
    private String uploadDirPath;

    private static final Logger logger = LoggerFactory.getLogger(VidsReportService.class);

    @Autowired
    public VidsReportService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String generateHighwayIncidentsReport(AtccReportRequest request) throws IOException {

        String pattern = "yyyy-MM-dd HH:mm:ss";
        request.setFrom(DateUtil.parseDateString(request.getFromDateString(), pattern));
        request.setTo(DateUtil.parseDateString(request.getToDateString(), pattern));

        QHighwayIncident HighwayIncident = new QHighwayIncident("highwayIncident");
        JPAQuery<HighwayIncident> query = new JPAQuery<>(entityManager);
        List<HighwayIncident> result = null;

        int page = 1;
        int offset = 0;
        int limit = 1000;

        query.select(HighwayIncident)
                .from(HighwayIncident)
                .where(HighwayIncident.incidentDate.between(request.getFrom(), request.getTo()))
                .orderBy(HighwayIncident.incidentDate.asc());

        if (request.feedId != 0) query.where(HighwayIncident.feed.id.eq(request.feedId));

        long totalRecordsCount = query.fetchCount();
        Path path = Paths.get(uploadDirPath);
        String filename = null;
        FileWriter fileWriter = null;

        switch (request.getReportFileType()) {
            case "csv":
                filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
                fileWriter = new FileWriter(filename);
                writeIncidentCSVHeader(fileWriter);
                while (totalRecordsCount > offset) {
                    offset = (page - 1) * limit;
                    if (offset > 0) {
                        query.offset(offset);
                    }
                    query.limit(limit);
                    result = query.fetch();
                    writeCSVValue(result, fileWriter);
                    page++;
                }
                break;
            case "json":
                filename = path.resolve(UUID.randomUUID().toString() + ".json").toString();
                fileWriter = new FileWriter(filename);
                while (totalRecordsCount > offset) {
                    offset = (page - 1) * limit;
                    if (offset > 0) {
                        query.offset(offset);
                    }
                    query.limit(limit);
                    result = query.fetch();
                    if (result.size() > 0) {
                        Gson gson = new Gson();
                        gson.toJson(result, fileWriter);
                    }
                    page++;
                }
                break;
        }
        if (fileWriter != null) {
            fileWriter.flush();
            fileWriter.close();
        }
        return filename;
    }

    private void writeCSVValue(List<HighwayIncident> result, FileWriter fileWriter) throws IOException {
        int i = 0;
        for (HighwayIncident event : result) {
            fileWriter.append(String.valueOf('"')).append(String.valueOf(i + 1)).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getIncidentDate(), "dd-MM-yyyy")).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getIncidentDate(), "HH:mm:ss")).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(event.getEventId()).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(event.getIncidentType().name()).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(event.getIncidentImage().concat(".png")).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(event.getIncidentVideo()).append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(event.getFeed() != null ? event.getFeed().getLocation() : "").append(String.valueOf('"'));
            fileWriter.append(',');
            fileWriter.append(String.valueOf('"')).append(event.getFeed() != null ? event.getFeed().getSite() : "").append(String.valueOf('"'));

            fileWriter.append('\n');
            i++;
        }
    }

    private void writeIncidentCSVHeader(FileWriter fileWriter) throws IOException {
        fileWriter.append("Sr. No");
        fileWriter.append(',');
        fileWriter.append("Incident Date");
        fileWriter.append(',');
        fileWriter.append("Incident Time");
        fileWriter.append(',');
        fileWriter.append("Incident ID");
        fileWriter.append(',');
        fileWriter.append("Incident Type");
        fileWriter.append(',');
        fileWriter.append("Incident Image");
        fileWriter.append(',');
        fileWriter.append("Incident Video");
        fileWriter.append(',');
        fileWriter.append("Location");
        fileWriter.append(',');
        fileWriter.append("Site");
        fileWriter.append('\n');
    }

    public String downloadHighwayIncidentsOnDailyBasis(AtccReportRequest request) throws IOException {
        List<HighwayIncidentType> incidentTypes = getHighwayIncidentTypes();

        String pattern = "yyyy-MM-dd HH:mm:ss";
        request.setFrom(DateUtil.parseDateString(request.getFromDateString(), pattern));
        request.setTo(DateUtil.parseDateString(request.getToDateString(), pattern));

        QHighwayIncident highwayIncident = new QHighwayIncident("highwayIncident");
        JPAQuery<HighwayIncident> query = new JPAQuery<>(entityManager);

        Date eventDate = null;
        String eventDateString;
        Long eventCount = null;
        HighwayIncidentType incidentType = null;

        List<com.querydsl.core.Tuple> result = null;
        Map<Date, List<VidsDaywiseReportResponse>> totalEventsByDate = new TreeMap<>();
        VidsDaywiseReportResponse response;
        List<VidsDaywiseReportResponse> responses;

        result = query
                .select(highwayIncident.incidentDate //0
                        , highwayIncident.incidentType, //1
                        highwayIncident.count() //2
                )
                .from(highwayIncident)
                .where(highwayIncident.incidentDate.between(request.getFrom(), request.getTo()))
                .groupBy(highwayIncident.incidentDate.dayOfMonth(), highwayIncident.incidentDate.month(), highwayIncident.incidentDate.year(), highwayIncident.incidentType)
                .orderBy(highwayIncident.incidentDate.asc())
                .fetch();

        com.querydsl.core.Tuple tuple;
        for (int i = 0; i < result.size(); i++) {
            tuple = result.get(i);

            eventDate = tuple.get(0, Date.class);
            incidentType = tuple.get(1, HighwayIncidentType.class);
            eventCount = tuple.get(2, Long.class);

            eventDateString = toFormattedDate(eventDate, "dd/MM/yyyy");
            eventDate = DateUtil.parseDateString(eventDateString, "dd/MM/yyyy");

            response = new VidsDaywiseReportResponse(eventDateString, incidentType, eventCount);
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
        for (HighwayIncidentType incType : incidentTypes) {
            fileWriter.append(incType.name());
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
            for (HighwayIncidentType incType : incidentTypes) {
                int count = 0;
                for (VidsDaywiseReportResponse vidsDaywiseReportResponse : totalEventsByDate.get(key)) {
                    if (incType.equals(vidsDaywiseReportResponse.getIncidentType())) {
                        count = vidsDaywiseReportResponse.getIncidentCount().intValue();
                        break;
                    }
                }
                fileWriter.append(String.valueOf('"')).append(String.valueOf(count)).append(String.valueOf('"'));
                fileWriter.append(',');
            }
            long totalEvents = 0;
            for (VidsDaywiseReportResponse vidsDaywiseReportResponse : totalEventsByDate.get(key)) {
                totalEvents = totalEvents + vidsDaywiseReportResponse.getIncidentCount();
            }
            fileWriter.append(String.valueOf('"')).append(String.valueOf(totalEvents)).append(String.valueOf('"'));
            fileWriter.append('\n');
            i++;
        }

        fileWriter.flush();
        fileWriter.close();
        return filename;
    }

    private List<HighwayIncidentType> getHighwayIncidentTypes() {
        QHighwayIncident highwayIncident = new QHighwayIncident("highwayIncident");
        return new JPAQuery<>(entityManager)
                .select(highwayIncident.incidentType)
                .distinct()
                .from(highwayIncident)
                .fetch();
    }
}
