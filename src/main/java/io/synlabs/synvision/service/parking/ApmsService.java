package io.synlabs.synvision.service.parking;

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
import java.io.IOException;
import java.nio.file.Path;
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

    public List<ParkingEvent> listAllParkingEvents(ParkingReportRequest request) {
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
                result = query
                        .select(parkingEvent)
                        .from(parkingEvent)
                        .where(parkingEvent.checkIn.between(request.getFrom(), request.getTo()))
                        .fetch();
                break;

        }

        return  result;
    }

    public Map<Date, ParkingReportResponse> listParkingEventsOnDailyBasis(ParkingReportRequest request) {
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
        List<ParkingReportResponse> response= new ArrayList<>();
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
                        parkingReportResponse.setCheckOut(checkoutCount);
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

        return  totalCheckinAndCheckoutsByDate;
    }

    public File export(List<ParkingEvent> parkingEvents, String reportType) {

        String reportName = "Parking  Event Report";
        List<String> header = getHeader();
        String[][] data = getExportData(parkingEvents);
        return createReport(reportType, reportName, header, data);
    }

    private String[][] getExportData(List<ParkingEvent> parkingEvents) {
        String data[][] = new String[parkingEvents.size()][5];
        for (int i = 0; i < parkingEvents.size(); i++) {
            ParkingEvent parkingEvent = parkingEvents.get(i);
            data[i][0] = "" + ++i; // for S.No index starts from zero
            --i;                   // back to original
            data[i][1] = parkingEvent.getEventId();
            data[i][2] = parkingEvent.getVehicleNo();
            data[i][3] = toFormattedDate(parkingEvent.getCheckIn(),"dd-MM-yyyy hh:mm:ss");
            data[i][4] = toFormattedDate(parkingEvent.getCheckOut(),"dd-MM-yyyy hh:mm:ss");
        }
        return data;
    }



    public List<String> getHeader() {
        List<String> header = new ArrayList<>();
        header.add("S. No");
        header.add("Event Id");
        header.add("Vehicle No");
        header.add("CheckIn");
        header.add("CheckOut");

        return header;
    }

    private String[][] getExportData1(Map<Date, ParkingReportResponse>totalCheckinAndCheckoutsByDate) {
        String data[][] = new String[totalCheckinAndCheckoutsByDate.size()][4];
        int i=0;
        for (Date key: totalCheckinAndCheckoutsByDate.keySet()) {
            data[i][0] = "" + ++i; // for S.No index starts from zero
            --i;                   // back to original
            data[i][1] = toFormattedDate(key,"dd/MM/yyyy");
            data[i][2] = String.valueOf(totalCheckinAndCheckoutsByDate.get(key).getCheckIn());
            data[i][3] = String.valueOf(totalCheckinAndCheckoutsByDate.get(key).getCheckOut());
            i++;
        }
        return data;
    }

    public File export1(Map<Date, ParkingReportResponse> totalCheckinAndCheckoutsByDate, String reportType) {

        String reportName = "Parking  Event Report";
        List<String> header = getHeader1();
        String[][] data = getExportData1(totalCheckinAndCheckoutsByDate);
        return createReport(reportType, reportName, header, data);
    }



    public List<String> getHeader1() {
        List<String> header = new ArrayList<>();
        header.add("S. No");
        header.add("Date");
        header.add("Total Check-Ins");
        header.add("Total Check-Outs");

        return header;
    }


    public File createReport(String reportType, String reportName, List<String> header, String[][] data) {
        File file = null;
        switch (reportType) {
            case "CSV":
                file = createCSV(header, data, new StringBuffer());
                break;
        }
        return file;
    }

    public StringBuffer writeCSVRow(List<String> list, StringBuffer data) {
        data = data == null ? new StringBuffer() : data;
        if (!list.isEmpty()) {
            for (String item : list) {
                data.append(String.valueOf('"')).append(item).append(String.valueOf('"')).append(",");
            }
            data.append("\n");
        }
        return data;
    }

    public StringBuffer writeCSVRows(String[][] rawData, StringBuffer data) {
        data = data == null ? new StringBuffer() : data;
        for (int i = 0; i < rawData.length; i++) {
            for (int j = 0; j < rawData[i].length; j++) {
                data.append(String.valueOf('"')).append(rawData[i][j]).append(String.valueOf('"')).append(",");
            }
            data.append("\n");
        }
        return data;
    }

    protected File writeCSV(StringBuffer data, String fileName) throws IOException {
        if (StringUtils.isEmpty(fileName)) {
            fileName = uploadDirPath + "/" + UUID.randomUUID().toString();
        }
        fileName = fileName.concat(".csv");
        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data.toString().getBytes());
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return file;
    }

    public File createCSV(List<String> list,String[][] rawData,  StringBuffer data){
        //list contains header ,rawdata is actual row data
//        header
        data = writeCSVRow(list, data);
//        body
        data = writeCSVRows(rawData, data);
        try {
            return writeCSV(data, "");
        } catch (IOException e) {
            logger.error("ERROR IN WRITING CSV",e);
            return null;
        }
    }
}
