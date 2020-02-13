package io.synlabs.synvision.service.parking;

import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.anpr.QAnprEvent;
import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.entity.parking.QParkingEvent;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.jpa.ImportStatusRepository;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.views.anpr.AnprFilterRequest;
import io.synlabs.synvision.views.anpr.AnprReportPageResponse;
import io.synlabs.synvision.views.anpr.AnprReportRequest;
import io.synlabs.synvision.views.anpr.AnprReportResponse;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.parking.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.SwitchPoint;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnprEventService extends BaseService {


    private static final Logger logger = LoggerFactory.getLogger(AnprEventService.class);
    @Autowired
    private EntityManager entityManager;


    @Value("${file.upload-dir}")
    private String uploadDirPath;


    public String downloadAnprEvents(AnprReportRequest request) throws IOException {
        int page = 1;
        int offset = 0;
        int limit = 1000;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
            request.setLpr(request.getLpr());

        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }

        QAnprEvent anprEvent = QAnprEvent.anprEvent;
        JPAQuery<AnprEvent> query = new JPAQuery<>(entityManager);
        List<AnprEvent> result = null;
        List<com.querydsl.core.Tuple> result1 = null;

        query.select(anprEvent)
                .from(anprEvent);
        if (!StringUtils.isEmpty(request.getLpr())) {
            query.where(anprEvent.anprText.like("%" + request.getLpr() + "%"));
        }
        if (request.getFrom() != null && request.getTo()==null) {
            query.where(anprEvent.eventDate.after(request.getFrom()).or(anprEvent.eventDate.eq(request.getFrom())));
        }
        if (request.getTo() != null && request.getFrom() ==null) {
            query.where(anprEvent.eventDate.before(request.getTo()).or(anprEvent.eventDate.eq(request.getTo())));
        }
        if (request.getFrom()!=null && request.getTo()!=null)
        {
            query.where(anprEvent.eventDate.between(request.getFrom(),request.getTo()));
        }
        query.orderBy(anprEvent.eventId.asc());

        long totalRecordsCount = query.fetchCount();
        Path path = Paths.get(uploadDirPath);
        String filename = null;
        FileWriter fileWriter = null;

        filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
        fileWriter = new FileWriter(filename);

        fileWriter.append("Sr. No");
        fileWriter.append(',');
        fileWriter.append("Date");
        fileWriter.append(',');
        fileWriter.append("Time");
        fileWriter.append(',');
        fileWriter.append("Lpr");
        fileWriter.append(',');
        fileWriter.append("Direction");
        fileWriter.append(',');
        fileWriter.append("Helmet?");
        fileWriter.append(',');
        fileWriter.append("Speed");
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
            for (AnprEvent event : result) {
                fileWriter.append(String.valueOf('"')).append(String.valueOf(i+1)).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getEventDate(), "dd/MM/yyyy")).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getEventDate(), "HH:mm:ss")).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(String.valueOf(event.getAnprText())).append(String.valueOf('"'));
                fileWriter.append(',');
                fileWriter.append(String.valueOf('"')).append(String.valueOf(event.getDirection())).append(String.valueOf('"'));
                fileWriter.append(',');
                String helmet;
                if (event.isHelmetMissing()) {
                    helmet = "Yes";
                } else {
                    helmet = "N/A";
                }
                fileWriter.append(String.valueOf('"')).append(helmet).append(String.valueOf('"'));
                fileWriter.append(',');
                String speed;
                if (event.getSpeed() == null) {
                    speed = "";
                } else {
                    speed = String.valueOf(event.getSpeed());
                }
                fileWriter.append(String.valueOf('"')).append(speed).append(String.valueOf('"'));
                fileWriter.append(',');
                String source = "";
                if (event.getSource() == null) {
                    source = "";
                } else {
                    speed = String.valueOf(event.getSource());
                }
                fileWriter.append(String.valueOf('"')).append(source).append(String.valueOf('"'));
                fileWriter.append('\n');
                i++;
            }
            page++;
        }

        fileWriter.flush();
        fileWriter.close();
        return filename;
    }


}
