package io.synlabs.synvision.service.parking;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.entity.parking.ParkingLot;
import io.synlabs.synvision.entity.parking.QParkingEvent;
import io.synlabs.synvision.enums.VehicleType;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import io.synlabs.synvision.jpa.ParkingLotRepository;
import io.synlabs.synvision.jpa.ParkingSlotRepository;
import io.synlabs.synvision.views.DashboardRequest;
import io.synlabs.synvision.views.parking.*;
import io.synlabs.synvision.views.incident.IncidentGroupCountResponse;
import io.synlabs.synvision.views.parking.VehicleCountResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ParkingGuidanceService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private ParkingEventRepository parkingEventRepository;
    @Autowired
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ParkingGuidanceService.class);

    public ParkingDashboardResponse stats(String lot) {

        ParkingLot parkingLot = parkingLotRepository.findOneByName(lot);
        ParkingDashboardResponse response = new ParkingDashboardResponse();
        response.setFreeSlots(parkingLot.getFreeSlots());
        response.setTotalSlots(parkingLot.getTotalSlots());
        response.setCarsParked(parkingLot.getBikesParked());
        response.setBikesParked(parkingLot.getCarsParked());
        response.setBikeSlots(parkingLot.getBikeSlots());
        response.setCarSlots(parkingLot.getCarSlots());
        return response;
    }

    public ParkingEventDashboardResponse getCheckedInVehicleCount() {
        long checkedInBikeCount = parkingEventRepository.countAllByCheckOutIsNullAndAndType(VehicleType.Bike);
        long checkedInCarCount = parkingEventRepository.countAllByCheckOutIsNullAndAndType(VehicleType.Car);

        ParkingEventDashboardResponse response = new ParkingEventDashboardResponse();
        response.setCheckedInBikes(checkedInBikeCount);
        response.setCheckedInCars(checkedInCarCount);

        return response;
    }

    //TODO stats by hour
    public List<HourlyStatsResponse> hourly(String lot) {
        return Collections.emptyList();
    }

    public ParkingEventCountResponse getParkingVehicleCount(DashboardRequest request) {
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


        ParkingEventCountResponse response = new ParkingEventCountResponse();
        response.setCheckInEvents(getCheckedInVehicleCount(request));
        response.setCheckOutEvents(getCheckedOutVehicleCount(request));
        return response;
    }

    public List<VehicleCountResponse> getCheckedInVehicleCount(DashboardRequest request) {

        List<VehicleCountResponse> checkInEvents = new ArrayList<>();
        QParkingEvent parkingEvent = QParkingEvent.parkingEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> result = null;

        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();

        switch (xAxis) {
            case "Hourly":
                result = query
                        .select(
                                parkingEvent.checkIn,
                                parkingEvent.count())
                        .from(parkingEvent)
                        .where(parkingEvent.checkIn.between(request.getFrom(), request.getTo()))
                        .groupBy(parkingEvent.checkIn.hour())
                        .fetch();

                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    Date date = tuple.get(0, Date.class);

                    calendar.setTime(date);
                    VehicleCountResponse eventCount = new VehicleCountResponse(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), "CheckIn", tuple.get(1, Long.class));

                    checkInEvents.add(eventCount);
                    result.set(i, null);
                }
                break;

            case "Daily":
            default:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                result = query
                        .select(
                                parkingEvent.checkIn,
                                parkingEvent.count())
                        .from(parkingEvent)
                        .where(parkingEvent.checkIn.between(request.getFrom(), request.getTo()))
                        .groupBy(parkingEvent.checkIn.dayOfMonth(), parkingEvent.checkIn.month(), parkingEvent.checkIn.year())
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    VehicleCountResponse eventCount = new VehicleCountResponse(formatter.format(tuple.get(0, Date.class)), "CheckIn", tuple.get(1, Long.class));

                    checkInEvents.add(eventCount);
                    result.set(i, null);
                }
                break;
        }
        return checkInEvents;
    }

    public List<VehicleCountResponse> getCheckedOutVehicleCount(DashboardRequest request) {

        List<VehicleCountResponse> checkInEvents = new ArrayList<>();
        QParkingEvent parkingEvent = QParkingEvent.parkingEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> result = null;

        String xAxis = StringUtils.isEmpty(request.getXAxis()) ? "" : request.getXAxis();

        switch (xAxis) {
            case "Hourly":
                result = query
                        .select(
                                parkingEvent.checkOut,
                                parkingEvent.count())
                        .from(parkingEvent)
                        .where(parkingEvent.checkOut.isNotNull())
                        .where(parkingEvent.checkOut.between(request.getFrom(), request.getTo()))
                        .groupBy(parkingEvent.checkOut.hour())
                        .fetch();

                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    Date date = tuple.get(0, Date.class);

                    calendar.setTime(date);
                    VehicleCountResponse eventCount = new VehicleCountResponse(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), "CheckOut", tuple.get(1, Long.class));

                    checkInEvents.add(eventCount);
                    result.set(i, null);
                }
                break;

            case "Daily":
            default:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                result = query
                        .select(
                                parkingEvent.checkOut,
                                parkingEvent.count())
                        .from(parkingEvent)
                        .where(parkingEvent.checkOut.between(request.getFrom(), request.getTo()))
                        .groupBy(parkingEvent.checkOut.dayOfMonth(), parkingEvent.checkOut.month(), parkingEvent.checkOut.year())
                        .fetch();
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
                    VehicleCountResponse eventCount = new VehicleCountResponse(formatter.format(tuple.get(0, Date.class)), "CheckOut", tuple.get(1, Long.class));

                    checkInEvents.add(eventCount);
                    result.set(i, null);
                }
                break;
        }
        return checkInEvents;
    }
}
