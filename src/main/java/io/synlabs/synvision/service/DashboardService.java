package io.synlabs.synvision.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.anpr.QAnprEvent;
import io.synlabs.synvision.entity.atcc.QAtccEvent;
import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.views.DashboardRequest;
import io.synlabs.synvision.views.DashboardResponse;
import io.synlabs.synvision.views.anpr.AnprVehicleCountResponse;
import io.synlabs.synvision.views.atcc.AtccVehicleCountResponse;
import io.synlabs.synvision.views.incident.IncidentCountResponse;
import io.synlabs.synvision.views.incident.IncidentGroupCountResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.text.ParseException;
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }

        QAtccEvent rawData = QAtccEvent.atccEvent;
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
                                rawData.eventDate.hour(),
                                rawData.type,
                                rawData.count())
                        .from(rawData)
                        .where(rawData.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(rawData.eventDate.hour(), rawData.type)
                        .fetch();

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
                                rawData.eventDate,
                                rawData.type,
                                rawData.count())
                        .from(rawData)
                        .where(rawData.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(rawData.eventDate, rawData.type)
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    date = tuple.get(0, Date.class);
                    vehicleType = tuple.get(rawData.type);
                    vehicleCount = tuple.get(2, Long.class);
                    String eventDateString = toFormattedDate(date,"dd/MM/yyyy");
                    response.add(new AtccVehicleCountResponse(eventDateString, vehicleType, vehicleCount));
                    result.set(i, null);
                }
                break;
        }


        return response;
    }

    public IncidentGroupCountResponse getIncidentsCount(DashboardRequest request) {
        //request.setFrom(BaseService.setMinTime(request.getFrom()));
        //request.setTo(BaseService.setMaxTime(request.getTo()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }


        IncidentGroupCountResponse response = new IncidentGroupCountResponse();
        response.setHelmetMissingIncidents(getHelmetMissingIncidents(request));
        response.setReverseDirectionIncidents(getReverseDirectionIncidents(request));
        return response;
    }


    public List<IncidentCountResponse> getHelmetMissingIncidents(DashboardRequest request) {

        List<IncidentCountResponse> helmetMissingIncidents = new ArrayList<>();
        QAnprEvent anprEvent = QAnprEvent.anprEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> result = null;

        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();

        switch (xAxis) {
            case "Hourly":
                result = query
                        .select(
                                anprEvent.eventDate,
                                anprEvent.count())
                        .from(anprEvent)
                        .where(anprEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .where(anprEvent.helmetMissing.isTrue())
                        .groupBy(anprEvent.eventDate.hour())
                        .fetch();

                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    Date date = tuple.get(0, Date.class);

                    calendar.setTime(date);
                    IncidentCountResponse incidentCount = new IncidentCountResponse(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), "helmetMissing", tuple.get(1, Long.class));

                    helmetMissingIncidents.add(incidentCount);
                    result.set(i, null);
                }
                break;

            case "Daily":
            default:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                result = query
                        .select(
                                anprEvent.eventDate,
                                anprEvent.count())
                        .from(anprEvent)
                        .where(anprEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .where(anprEvent.helmetMissing.isTrue())
                        .groupBy(anprEvent.eventDate.dayOfMonth(), anprEvent.eventDate.month(), anprEvent.eventDate.year())
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    IncidentCountResponse incidentCount = new IncidentCountResponse(formatter.format(tuple.get(0, Date.class)), "helmetMissing", tuple.get(1, Long.class));

                    helmetMissingIncidents.add(incidentCount);
                    result.set(i, null);
                }
                break;
        }
        return helmetMissingIncidents;
    }


    public List<IncidentCountResponse> getReverseDirectionIncidents(DashboardRequest request) {

        List<IncidentCountResponse> reverseDirectionIncidents = new ArrayList<>();
        QAnprEvent anprEvent = QAnprEvent.anprEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> result = null;

        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();

        switch (xAxis) {
            case "Hourly":
                result = query
                        .select(
                                anprEvent.eventDate,
                                anprEvent.count())
                        .from(anprEvent)
                        .where(anprEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .where(anprEvent.direction.eq("rev"))
                        .groupBy(anprEvent.eventDate.hour())
                        .fetch();

                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    Date date = tuple.get(0, Date.class);
                    calendar.setTime(date);
                    IncidentCountResponse incidentCount = new IncidentCountResponse(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), "rev", tuple.get(1, Long.class));

                    reverseDirectionIncidents.add(incidentCount);
                    result.set(i, null);
                }
                break;

            case "Daily":
            default:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                result = query
                        .select(
                                anprEvent.eventDate,
                                anprEvent.count())
                        .from(anprEvent)
                        .where(anprEvent.eventDate.between(request.getFrom(), request.getTo()))
                        .where(anprEvent.direction.eq("rev"))
                        .groupBy(anprEvent.eventDate.dayOfMonth(), anprEvent.eventDate.month(), anprEvent.eventDate.year())
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    IncidentCountResponse incidentCount = new IncidentCountResponse(formatter.format(tuple.get(0, Date.class)), "rev", tuple.get(1, Long.class));

                    reverseDirectionIncidents.add(incidentCount);
                    result.set(i, null);
                }
                break;
        }
        return reverseDirectionIncidents;
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

    public List<AnprVehicleCountResponse> getAnprCount(DashboardRequest request) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            request.setFrom(sdf.parse(request.getFromDateString()));
            request.setTo(sdf.parse(request.getToDateString()));
        } catch (ParseException e) {
            logger.info("Couldn't parse date", request.getFrom());
        }

        QAnprEvent rawData = QAnprEvent.anprEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> result = null;
        Date date = null;
        Integer timeSpan = null;
        String vehicleType = null;
        Long vehicleCount = null;
        List<AnprVehicleCountResponse> response = new ArrayList<>();
        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();

        switch (xAxis) {
            case "Hourly":
                result = query
                        .select(
                                rawData.eventDate.hour(),
                                rawData.vehicleClass,
                                rawData.count())
                        .from(rawData)
                        .where(rawData.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(rawData.eventDate.hour(), rawData.vehicleClass)
                        .fetch();

                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    timeSpan = tuple.get(0, Integer.class);
                    vehicleType = tuple.get(rawData.vehicleClass);
                    vehicleCount = tuple.get(2, Long.class);
                    response.add(new AnprVehicleCountResponse(timeSpan.toString(), vehicleType, vehicleCount));
                    result.set(i, null);
                }
                break;

            case "Daily":
            default:
                result = query
                        .select(
                                rawData.eventDate,
                                rawData.vehicleClass,
                                rawData.count())
                        .from(rawData)
                        .where(rawData.eventDate.between(request.getFrom(), request.getTo()))
                        .groupBy(rawData.eventDate, rawData.vehicleClass)
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    date = tuple.get(0, Date.class);
                    vehicleType = tuple.get(rawData.vehicleClass);
                    vehicleCount = tuple.get(2, Long.class);
                    String eventDateString = toFormattedDate(date,"dd/MM/yyyy");
                    response.add(new AnprVehicleCountResponse(eventDateString, vehicleType, vehicleCount));
                    result.set(i, null);
                }
                break;
        }


        return response;
    }
}
