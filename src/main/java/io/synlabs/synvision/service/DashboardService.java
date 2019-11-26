package io.synlabs.synvision.service;

import io.synlabs.synvision.jpa.AnprRepository;
import io.synlabs.synvision.views.DashboardRequest;
import io.synlabs.synvision.views.DashboardResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by itrs on 10/23/2019.
 */
@Service
public class DashboardService extends BaseService {

    @Autowired
    private AnprRepository anprRepository;


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

                int count = anprRepository.countAllByOrgAndEventDateBetween(getAtccUser().getOrg(), dateIter, DateUtils.addHours(dateIter, 1));
                DashboardResponse response = new DashboardResponse(time, count);
                responses.add(response);
            }

        } catch (Exception e) {

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
                int count = anprRepository.countAllByOrgAndEventDateBetween(getAtccUser().getOrg(), fD, tD);
                DashboardResponse response = new DashboardResponse(date2, count);
                responses.add(response);
            } catch (Exception e) {

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
                int count = anprRepository.countAllByOrgAndEventDateBetween(getAtccUser().getOrg(), convertedFromDate, lastDayOfMonth);
                DashboardResponse response = new DashboardResponse(month, count);
                responses.add(response);

            } catch (Exception e) {

            }
        }

        return responses;
    }
}
