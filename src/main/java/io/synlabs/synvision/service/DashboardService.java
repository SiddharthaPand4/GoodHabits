package io.synlabs.synvision.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.controller.atcc.AtccDataController;
import io.synlabs.synvision.entity.anpr.QAnprEvent;
import io.synlabs.synvision.entity.atcc.AtccRawData;
import io.synlabs.synvision.entity.atcc.QAtccRawData;
import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.views.DashboardRequest;
import io.synlabs.synvision.views.DashboardResponse;
import io.synlabs.synvision.views.atcc.AtccVehicleCountResponse;
import io.synlabs.synvision.views.incident.IncidentCountResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by itrs on 10/23/2019.
 */
@Service
public class DashboardService extends BaseService {

    @Autowired
    private AnprEventRepository anprEventRepository;
    @Autowired
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);


    public List<AtccVehicleCountResponse> getAtccVehicleCount(DashboardRequest request) {

        logger.info("request fromDate:"+request.from);
        logger.info("request toDate:"+request.to);
        QAtccRawData rawData = QAtccRawData.atccRawData;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> result = null;
        Date date = null;
        Integer timeSpan = null;
        String vehicleType = null;
        Long vehicleCount = null;
        List<AtccVehicleCountResponse> response = new ArrayList<>();
        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();

        switch (xAxis) {
            case "Hourly":
                result = query
                        .select(
                                rawData.time.hour(),
                                rawData.type,
                                rawData.count())
                        .from(rawData)
                        .where(rawData.date.between(request.from, request.to))
                        .groupBy(rawData.time.hour(), rawData.type)
                        .fetch();
                logger.info(query.toString());
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    timeSpan = tuple.get(0, Integer.class);
                    vehicleType = tuple.get(rawData.type);
                    vehicleCount = tuple.get(2, Long.class);
                    response.add(new AtccVehicleCountResponse(timeSpan.toString(), vehicleType, vehicleCount));
                    result.set(i, null);
                }
                break;

            case "Daily":
            default:
                result = query
                        .select(
                                rawData.date,
                                rawData.type,
                                rawData.count())
                        .from(rawData)
                        .where(rawData.date.between(request.from,request.to))
                        .groupBy(rawData.date, rawData.type)
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    date = tuple.get(0, Date.class);
                    vehicleType = tuple.get(rawData.type);
                    vehicleCount = tuple.get(2, Long.class);
                    response.add(new AtccVehicleCountResponse(date.toString(), vehicleType, vehicleCount));
                    result.set(i, null);
                }
                break;
        }


        return response;
    }

    public List<IncidentCountResponse> getIncidentVehicleCount(DashboardRequest request) {

        String incidentType = "Helmet-missing";
        Date date = null;
        Long vehicleCount = null;
        List<IncidentCountResponse> response = new ArrayList<>();
        List<Tuple> result = null;

        QAnprEvent anprEvent = QAnprEvent.anprEvent;

        result = getIncidentCount(request, incidentType, anprEvent);
        for (int i = 0; i < result.size(); i++) {
            Tuple tuple = result.get(i);
            date = tuple.get(anprEvent.eventDate);
            vehicleCount = tuple.get(1, Long.class);
            response.add(new IncidentCountResponse(date, incidentType, vehicleCount));
            result.set(i, null);
        }

        incidentType = "reverse-direction";
        result = getIncidentCount(request, incidentType, anprEvent);
        for (int i = 0; i < result.size(); i++) {
            Tuple tuple = result.get(i);
            date = tuple.get(anprEvent.eventDate);
            vehicleCount = tuple.get(1, Long.class);
            response.add(new IncidentCountResponse(date, incidentType, vehicleCount));
            result.set(i, null);
        }
        return response;
    }

    private List<Tuple> getIncidentCount(DashboardRequest request, String incidentType, QAnprEvent anprEvent) {
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);

        query = query.select(
                anprEvent.eventDate,
                anprEvent.count())
                .from(anprEvent)
                .where(anprEvent.eventDate.after(request.from))
                .where(anprEvent.eventDate.before(request.to));

        switch (incidentType) {
            case "Helmet-missing":
                query = query.where(anprEvent.helmetMissing.isTrue());
                break;
            case "reverse-direction":
                query = query.where(anprEvent.direction.eq("rev"));
                break;

        }


        return query.groupBy(anprEvent.eventDate.dayOfMonth(), anprEvent.eventDate.month(), anprEvent.eventDate.year())
                .fetch();
    }

    public List<DashboardResponse> getTotalNoOfVehiclesByDateFilter(DashboardRequest request) {
        List<DashboardResponse> responses = new ArrayList<>();

        switch (request.getFilterType()) {
            case "today":
            case "yesterday":
                responses = getTotalNoOfVehiclesBySelectedDate(request);
                break;

            case "last7days":
            case "custom":
                responses = getTotalNoOfVehiclesBetweenTwoDates(request);
                break;

            case "last3months":
            case "last6months":
                responses = getTotalNoOfVehiclesByMonth(request);
                break;


        }

        return responses;
    }

    public List<DashboardResponse> getTotalNoOfVehiclesBySelectedDate(DashboardRequest request) {

        List<DashboardResponse> responses = new ArrayList<>();

        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = "";

        if (request.getFilterType().equals("today")) {
            date = localDateFormat.format(new Date());
        } else if (request.getFilterType().equals("yesterday")) {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            date = localDateFormat.format(cal.getTime());
        }

        String fdate = date + " " + "00:00:00";
        String tDate = date + " " + "23:59:59";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date fD = dateFormat.parse(fdate);
            Date tD = dateFormat.parse(tDate);

            for (Date dateIter = fD; !dateIter.after(tD); dateIter = DateUtils.addHours(dateIter, 1)) {
                SimpleDateFormat localDateFormat1 = new SimpleDateFormat("HH:mm");
                String time = localDateFormat1.format(dateIter);

                int count = anprEventRepository.countAllByEventDateBetweenAndArchivedFalse(dateIter, DateUtils.addHours(dateIter, 1));
                DashboardResponse response = new DashboardResponse(time, count);
                responses.add(response);
            }

        } catch (Exception e) {
            //TODO
        }

        return responses;
    }


    public List<DashboardResponse> getTotalNoOfVehiclesBetweenTwoDates(DashboardRequest request) {

        List<DashboardResponse> responses = new ArrayList<>();
        LocalDateTime localDateTo = null;
        LocalDateTime localDateFrom = null;
        if (request.getTo() != null) {
            localDateTo = LocalDateTime.fromDateFields(request.getTo());
        }

        if (request.getFrom() != null) {
            localDateFrom = LocalDateTime.fromDateFields(request.getFrom());
        }

        if (!StringUtils.isEmpty(request.getFilterType()) && request.getFilterType().equals("last7days")) {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            localDateFrom = LocalDateTime.fromDateFields(cal.getTime());

            final Calendar cal1 = Calendar.getInstance();
            cal1.add(Calendar.DATE, -1);
            localDateTo = LocalDateTime.fromDateFields(cal1.getTime());
        }


        for (LocalDateTime date = localDateFrom.plusDays(1); date.isBefore(localDateTo.plusDays(1)); date = date.plusDays(1)) {
            String key = date.toDate().toInstant().toString();
            SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int n = key.indexOf("T");
            String date2 = key.substring(0, n);
            String fdate = date2 + " " + "00:00:00";
            String tDate = date2 + " " + "23:59:59";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date fD = dateFormat.parse(fdate);
                Date tD = dateFormat.parse(tDate);
                int count = anprEventRepository.countAllByEventDateBetweenAndArchivedFalse(fD, tD);
                DashboardResponse response = new DashboardResponse(date2, count);
                responses.add(response);
            } catch (Exception e) {
                //TODO
            }

        }

        return responses;
    }


    public List<DashboardResponse> getTotalNoOfVehiclesByMonth(DashboardRequest request) {
        List<DashboardResponse> responses = new ArrayList<>();

        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        String firstDayOfMonth = "";
        int totalmonths = 0;
        if (request.getFilterType().equals("last3months")) {
            totalmonths = 3;
        } else if (request.getFilterType().equals("last6months")) {
            totalmonths = 6;
        }

        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(totalmonths);

        int startMonth = earlier.getMonth().getValue();

        for (int i = startMonth; i < startMonth + totalmonths; i++) {
            firstDayOfMonth = year + "/" + i + "/" + "01";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

            try {
                Date convertedFromDate = dateFormat.parse(firstDayOfMonth);

                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(convertedFromDate);

                calendar1.add(Calendar.MONTH, 1);
                calendar1.set(Calendar.DAY_OF_MONTH, 1);
                calendar1.add(Calendar.DATE, -1);

                Date lastDayOfMonth = calendar1.getTime();

                String month = new SimpleDateFormat("MMM").format(calendar1.getTime());
                int count = anprEventRepository.countAllByEventDateBetweenAndArchivedFalse(convertedFromDate, lastDayOfMonth);
                DashboardResponse response = new DashboardResponse(month, count);
                responses.add(response);

            } catch (Exception e) {
                //TODO
            }
        }

        return responses;
    }
}
