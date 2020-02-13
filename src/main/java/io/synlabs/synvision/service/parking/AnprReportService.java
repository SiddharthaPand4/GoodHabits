package io.synlabs.synvision.service.parking;

import com.opencsv.CSVWriter;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.anpr.QAnprEvent;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.views.anpr.AnprReportRequest;
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
public class AnprReportService extends BaseService {


    private static final Logger logger = LoggerFactory.getLogger(AnprReportService.class);
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
        if (request.getFrom() != null) {
            query.where(anprEvent.eventDate.after(request.getFrom()).or(anprEvent.eventDate.eq(request.getFrom())));
        }
        if (request.getTo() != null) {
            query.where(anprEvent.eventDate.before(request.getTo()).or(anprEvent.eventDate.eq(request.getTo())));
        }
        query.orderBy(anprEvent.eventId.asc());

        long totalRecordsCount = query.fetchCount();
        Path path = Paths.get(uploadDirPath);
        String filename = null;
        FileWriter fileWriter = null;

        filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
        fileWriter = new FileWriter(filename);
        CSVWriter writer=new CSVWriter(fileWriter);
        String record []="Sr.No,Date,Time,Lpr,Direction,Helmet?,Speed,Location".split(",");
        writer.writeNext(record);
        while (totalRecordsCount > offset) {
            offset = (page - 1) * limit;
            if (offset > 0) {
                query.offset(offset);
            }
            query.limit(limit);
            result = query.fetch();

            int i = 1;
            for (AnprEvent event : result) {
                String report[]=new String[10];
                report[0]=String.valueOf(i);
                report[1]=String.valueOf(toFormattedDate(event.getEventDate(), "dd/MM/yyyy"));
                report[2]=String.valueOf(toFormattedDate(event.getEventDate(), "HH:mm:ss"));
                report[3]=String.valueOf(event.getAnprText());
                report[4]=String.valueOf(event.getDirection());
                String helmet;
                if (event.isHelmetMissing()) {
                    helmet = "Yes";
                } else {
                    helmet = "N/A";
                }
                report[5]=helmet;
                String speed;
                if (event.getSpeed() == null) {
                    speed = "";
                } else {
                    speed = String.valueOf(event.getSpeed());
                }
                report[6]=speed;
                String source = "";
                if (event.getSource() == null) {
                    source = "";
                } else {
                    source= String.valueOf(event.getSource());
                }
                report[7]=source;
                writer.writeNext(report);
                i++;
            }
            page++;
        }

        fileWriter.flush();
        fileWriter.close();
        return filename;
    }


}
