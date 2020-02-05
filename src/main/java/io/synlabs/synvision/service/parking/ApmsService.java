package io.synlabs.synvision.service.parking;

import com.google.gson.Gson;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.parking.QParkingEvent;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.ImportStatusRepository;
import io.synlabs.synvision.service.BaseService;
import io.synlabs.synvision.views.parking.*;
import io.synlabs.synvision.views.common.PageResponse;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ApmsService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(ApmsService.class);
    private Path fileStorageLocation;

    @Autowired
    private ParkingEventRepository parkingEventRepository;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ImportStatusRepository statusRepository;

    @Value("${file.upload-dir}")
    private String uploadDirPath;


    public PageResponse<ApmsResponse> eventsList(ApmsFilterRequest request) {

        QParkingEvent event = QParkingEvent.parkingEvent;
        JPAQuery<ParkingEvent> query = new JPAQuery<>(entityManager);

        query = query.select(event).from(event);


        if (!StringUtils.isEmpty(request.getVehicleNo())) {
            query = query.where(event.vehicleNo.eq(request.getVehicleNo()));
        }
        if (StringUtils.isEmpty(null)) {
            query = query.select(event).from(event);
        }

        query.orderBy(event.checkIn.desc());

        //pagination start
        int count = (int) query.fetchCount();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }
        //pagination ends


        List<ParkingEvent> data = query.fetch();
        List<ApmsResponse> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            ApmsResponse res = new ApmsResponse(item);
            list.add(res);
        });
        return (PageResponse<ApmsResponse>) new ApmsPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public void checkIn(String vehicleNo) {

        ParkingEvent parkingEvent = new ParkingEvent();
        if (!StringUtils.isEmpty(vehicleNo)) {
            parkingEvent.setVehicleNo(vehicleNo);
            parkingEvent.setCheckIn(new Date());
            parkingEvent.setEventId(UUID.randomUUID().toString());
            parkingEventRepository.saveAndFlush(parkingEvent);
        }
    }

    public void checkOut(String vehicleNo) {

        ParkingEvent parkingEvent = parkingEventRepository.findFirstByVehicleNoAndCheckOutOrderByIdDesc(vehicleNo, null);
        parkingEvent.setVehicleNo(vehicleNo);
        parkingEvent.setCheckOut(new Date());
        parkingEventRepository.saveAndFlush(parkingEvent);

    }

    public ApmsResponse eventStatus(String vehicleNo) {
        ApmsResponse response = null;
        if (StringUtils.isEmpty(vehicleNo)) {
            throw new ValidationException("Empty Vehicle No");
        }
        QParkingEvent event = QParkingEvent.parkingEvent;
        JPAQuery<ParkingEvent> query = new JPAQuery<>(entityManager);

        ParkingEvent parkingEvent = query
                .select(event)
                .from(event)
                .where((event.vehicleNo.eq(vehicleNo)))
                .orderBy(event.checkIn.desc())
                .fetchFirst();
        if(parkingEvent!=null){
            response = new ApmsResponse(parkingEvent);
        }

        return response;

    }

    public String downloadParkingEvents(ParkingReportRequest request) throws IOException {
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

        QParkingEvent parkingEvent = QParkingEvent.parkingEvent;
        JPAQuery<ParkingEvent> query = new JPAQuery<>(entityManager);
        List<ParkingEvent> result = null;
        List<com.querydsl.core.Tuple> result1 = null;
        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();

        switch (xAxis) {
            case "All":
                      query
                        .select(parkingEvent)
                        .from(parkingEvent)
                        .where(parkingEvent.checkIn.between(request.getFrom(), request.getTo()));
                break;

        }
        long totalRecordsCount = query.fetchCount();
        Path path = Paths.get(uploadDirPath);
        String filename = null;
        FileWriter fileWriter = null;

        switch(request.getReportType()) {
            case "CSV":
            filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
            fileWriter = new FileWriter(filename);

            fileWriter.append("Sr. No");
            fileWriter.append(',');
            fileWriter.append("EventId");
            fileWriter.append(',');
            fileWriter.append("Vehicle No");
            fileWriter.append(',');
            fileWriter.append("CheckIn Date");
            fileWriter.append(',');
            fileWriter.append("CheckIn Time");
            fileWriter.append(',');
            fileWriter.append("CheckOut");
            fileWriter.append(',');
            fileWriter.append("CheckOut Time");
            fileWriter.append('\n');
            while (totalRecordsCount > offset) {
                offset = (page - 1) * limit;
                if (offset > 0) {
                    query.offset(offset);
                }
                query.limit(limit);
                result = query.fetch();

                int i = 0;
                for (ParkingEvent event : result) {
                    fileWriter.append(String.valueOf('"')).append(String.valueOf(i + 1)).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(event.getEventId()).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(event.getVehicleNo()).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getCheckIn(), "dd-MM-yyyy")).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getCheckIn(), "HH:mm:ss")).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getCheckOut(), "dd-MM-yyyy")).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(toFormattedDate(event.getCheckOut(), "HH:mm:ss")).append(String.valueOf('"'));

                    fileWriter.append('\n');
                }
                page++;
            }
            break;

            case "JSON":
                filename = path.resolve(UUID.randomUUID().toString() + ".json").toString();
                fileWriter = new FileWriter(filename);
                while (totalRecordsCount > offset) {
                    offset = (page - 1) * limit;
                    if (offset > 0) {
                        query.offset(offset);
                    }
                    query.limit(limit);

                    result = query.fetch();


                    Gson gson = new Gson();

                    for (ParkingEvent event : result) {
                        ParkingEventResponse response = new ParkingEventResponse(event);
                        gson.toJson(response, fileWriter);
                    }
                    page++;
                }

                break;
        }


        fileWriter.flush();
        fileWriter.close();
        return filename;
    }

    public String downloadParkingEventsOnDailyBasis(ParkingReportRequest request) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }

        QParkingEvent parkingEvent = QParkingEvent.parkingEvent;
        JPAQuery<ParkingEvent> checkinquery = new JPAQuery<>(entityManager);
        JPAQuery<ParkingEvent> checkoutquery = new JPAQuery<>(entityManager);
        Date checkin = null;
        Date checkout = null;
        Long checkoutCount= null ;
        Long checkinCount = null;
        List<com.querydsl.core.Tuple> result = null;
        List<com.querydsl.core.Tuple> result1 = null;
        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();
        Map<Date, ParkingReportResponse> totalCheckinAndCheckoutsByDate = new TreeMap<Date, ParkingReportResponse>();

        switch (xAxis) {

            case "Daily":
                result = checkinquery
                        .select(parkingEvent.checkIn,
                                parkingEvent.count())
                        .from(parkingEvent)
                        .where(parkingEvent.checkIn.between(request.getFrom(),request.getTo()))
                        .groupBy(parkingEvent.checkIn.dayOfMonth(), parkingEvent.checkIn.month(), parkingEvent.checkIn.year())
                        .orderBy(parkingEvent.checkIn.asc())
                        .fetch();

                result1 = checkoutquery
                        .select(parkingEvent.checkOut,
                                parkingEvent.count())
                        .from(parkingEvent)
                        .where(parkingEvent.checkOut.between(request.getFrom(),request.getTo()))
                        .groupBy(parkingEvent.checkOut.dayOfMonth(), parkingEvent.checkOut.month(), parkingEvent.checkOut.year())
                        .orderBy(parkingEvent.checkIn.asc())
                        .fetch();

                for (int i = 0; i < result.size(); i++) {
                    com.querydsl.core.Tuple tuple = result.get(i);

                    checkin = tuple.get(0, Date.class);
                    String checkinString = toFormattedDate(checkin,"dd/MM/yyyy");

                    try {
                        checkin= sdf1.parse(checkinString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    checkinCount = tuple.get(1, Long.class);

                    totalCheckinAndCheckoutsByDate.put(checkin,new ParkingReportResponse(checkinCount,checkoutCount));
                    result.set(i, null);
                }

                for (int i = 0; i < result1.size(); i++) {
                    com.querydsl.core.Tuple tuple = result1.get(i);
                    checkout = tuple.get(0, Date.class);

                    String checkOutString = toFormattedDate(checkout,"dd/MM/yyyy");

                    try {
                        checkout= sdf1.parse(checkOutString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    checkoutCount = tuple.get(1, Long.class);

                    if(totalCheckinAndCheckoutsByDate.containsKey(checkout)){
                        ParkingReportResponse parkingReportResponse = totalCheckinAndCheckoutsByDate.get(checkout);
                        parkingReportResponse.setTotalCheckOut(checkoutCount);
                        totalCheckinAndCheckoutsByDate.put(checkout,parkingReportResponse);
                    }

                    else{
                        checkinCount=null;
                        totalCheckinAndCheckoutsByDate.put(checkout,new ParkingReportResponse(checkinCount,checkoutCount));
                    }

                    result1.set(i, null);
                }
                break;
        }
        List<ParkingReportResponse> responses = new ArrayList<>();
        for(Date date: totalCheckinAndCheckoutsByDate.keySet()){
            responses.add(new ParkingReportResponse( totalCheckinAndCheckoutsByDate.get(date).getTotalCheckIn(),totalCheckinAndCheckoutsByDate.get(date).getTotalCheckOut(),toFormattedDate(date,"dd/MM/yyyy")));
        }

        Path path = Paths.get(uploadDirPath);
        String filename=null;
        FileWriter fileWriter= null;
        switch (request.getReportType()){
            case "CSV":
                filename = path.resolve(UUID.randomUUID().toString() + ".csv").toString();
                fileWriter = new FileWriter(filename);
                fileWriter.append("Sr. No");
                fileWriter.append(',');
                fileWriter.append("Date");
                fileWriter.append(',');
                fileWriter.append("Total Check-Ins");
                fileWriter.append(',');
                fileWriter.append("Total Check-Outs");
                fileWriter.append('\n');

                int i=0;
                for (ParkingReportResponse response1: responses) {

                    fileWriter.append(String.valueOf('"')).append(String.valueOf(i+1)).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(response1.getDate()).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(String.valueOf(response1.getTotalCheckIn())).append(String.valueOf('"'));
                    fileWriter.append(',');
                    fileWriter.append(String.valueOf('"')).append(String.valueOf(response1.getTotalCheckOut())).append(String.valueOf('"'));

                    fileWriter.append('\n');

                }
                break;

            case "JSON":
                filename = path.resolve(UUID.randomUUID().toString() + ".json").toString();
                fileWriter = new FileWriter(filename);

                Gson gson = new Gson();

                for (ParkingReportResponse response1 : responses) {
                     gson.toJson(response1, fileWriter);
                }

                break;

        }


        fileWriter.flush();
        fileWriter.close();
        return  filename;
    }

}
