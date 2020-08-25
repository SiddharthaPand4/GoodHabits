package io.synlabs.synvision.service.parking;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.entity.parking.ParkingLot;
import io.synlabs.synvision.entity.parking.ParkingSlot;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.entity.parking.QParkingEvent;
import io.synlabs.synvision.enums.VehicleType;

import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import io.synlabs.synvision.jpa.ParkingLotRepository;
import io.synlabs.synvision.jpa.ParkingSlotRepository;
import io.synlabs.synvision.service.AtccDataService;
import io.synlabs.synvision.views.DashboardRequest;
import io.synlabs.synvision.views.common.DummyRequest;
import io.synlabs.synvision.views.common.DummyResponse;
import io.synlabs.synvision.views.parking.*;
import io.synlabs.synvision.views.parking.VehicleCountResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.synlabs.synvision.entity.parking.QParkingLot.parkingLot;

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

    @Autowired
    private AtccDataService atccDataService;

    @Autowired
    private AnprEventRepository anprEventRepository;


    @Autowired
    private FileStorageProperties fileStorageProperties;
    private Path fileStorageLocation;

    private static final Logger logger = LoggerFactory.getLogger(ParkingGuidanceService.class);


    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public ParkingDashboardResponse stats(String lotName) {

        ParkingLot lot = parkingLotRepository.findOneByName(lotName);


        long totalSlots = parkingSlotRepository.countByLot(lot);
        long totalFreeSlots = parkingSlotRepository.countByLotAndFree(lot, true);
        long totalParkedSlots = parkingSlotRepository.countByLotAndFree(lot, false);
        long totalParkedMisalignedSlots = parkingSlotRepository.countByLotAndFreeAndMisaligned(lot, false, true);
        long carTotalSlots = parkingSlotRepository.countByLotAndVehicleType(lot, VehicleType.Car);
        long bikeTotalSlots = parkingSlotRepository.countByLotAndVehicleType(lot, VehicleType.Bike);
        long carsParked = parkingSlotRepository.countByLotAndFreeAndVehicleType(lot, false, VehicleType.Car);
        long bikesParked = parkingSlotRepository.countByLotAndFreeAndVehicleType(lot, false, VehicleType.Bike);

        ParkingDashboardResponse response = new ParkingDashboardResponse();
        response.setTotalSlots((int) totalSlots);
        response.setFreeSlots((int) totalFreeSlots);
        response.setParkedSlots((int) totalParkedSlots);
        response.setParkedMisalignedSlots((int) totalParkedMisalignedSlots);

        response.setCarsParked((int) carsParked);
        response.setBikesParked((int) bikesParked);

        response.setCarSlots((int) carTotalSlots);
        response.setBikeSlots((int) bikeTotalSlots);

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
                                parkingEvent.checkIn.hour(),  //.hour() for preventing full group by exception
                                parkingEvent.count())
                        .from(parkingEvent)
                        .where(parkingEvent.checkIn.between(request.getFrom(), request.getTo()))
                        .groupBy(parkingEvent.checkIn.hour())
                        .fetch();

                //Calendar calendar = Calendar.getInstance();//
                for (int i = 0; i < result.size(); i++) {
                    Tuple tuple = result.get(i);
//                    Date date = tuple.get(0, Date.class);

                    Integer hour = tuple.get(0, Integer.class);
                    VehicleCountResponse eventCount = new VehicleCountResponse(hour.toString(), "CheckIn", tuple.get(1, Long.class));

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

    public List<ParkingSlot> slots(String lot) {
        ParkingLot parkingLot = parkingLotRepository.findOneByName(lot);

        if (parkingLot == null) {
            throw new NotFoundException("Cannot locate lot" + lot);
        }

        return parkingSlotRepository.findAllByLotNameOrderByName(lot);
    }

    public void updateSlot(UpdateSlotRequest request) {
        ParkingSlot slot = parkingSlotRepository.findOneByNameAndLotName(request.getSlot(), request.getLot());
        if (slot == null) {
            throw new NotFoundException("Cannot found slot:" + request.getSlot());
        }

        //slot is free -> occupied
        if (slot.isFree() && !request.isStatus()) {
            slot.setLastOccupied(new Date());
        }

        //slot is occupied -> free
        if (!slot.isFree() && request.isStatus()) {
            slot.setLastOccupied(null);
        }

        slot.setFree(request.isStatus());
        slot.setMisaligned(request.isMisaligned());

        parkingSlotRepository.saveAndFlush(slot);

        ParkingLot lot = parkingLotRepository.findOneByName(slot.getLot().getName());
        int freeSlots = lot.getFreeSlots();
        int carsParked = lot.getCarsParked();
        int bikesParked = lot.getBikesParked();


        if (request.isStatus()) {
            freeSlots = freeSlots + 1;
            if (slot.getVehicleType() != null) {

                if (slot.getVehicleType().equals(VehicleType.Car)) {
                    carsParked = carsParked - 1;
                }
                if (slot.getVehicleType().equals(VehicleType.Bike)) {
                    bikesParked = bikesParked - 1;
                }
            }
        } else {
            freeSlots = freeSlots - 1;
            if (slot.getVehicleType() != null) {

                if (slot.getVehicleType().equals(VehicleType.Car)) {
                    carsParked = carsParked + 1;
                }
                if (slot.getVehicleType().equals(VehicleType.Bike)) {
                    bikesParked = bikesParked + 1;
                }
            }
        }

        lot.setFreeSlots(freeSlots);
        lot.setCarsParked(carsParked);
        lot.setBikesParked(bikesParked);
        parkingLotRepository.saveAndFlush(lot);
    }

    public void updateParkingLotImage(String lotName, String imageName) {
        ParkingLot parkingLot = parkingLotRepository.findOneByName(lotName);
        parkingLot.setLastestImage(imageName);
        parkingLotRepository.saveAndFlush(parkingLot);
    }

    public Resource downloadLotImage(String lotName) {
        String filename = null;
        Resource resource = null;
        try {
            ParkingLot parkingLot = parkingLotRepository.findOneByName(lotName);
            if (parkingLot != null) {
                filename = parkingLot.getLastestImage();

                if (!StringUtils.isEmpty(filename)) {
                    Path filePath = Paths.get(this.fileStorageLocation.toString(), "pgs", filename).toAbsolutePath().normalize();
                    resource = new UrlResource(filePath.toUri());
                }
            }
            if (resource != null && resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + filename, ex);
        }
    }

    public Resource downloadParkingEventImage(Long mid) {
        Resource resource = null;
        long id = new DummyRequest().unmask(mid);
        Optional<ParkingEvent> parkingEventOptional = parkingEventRepository.findById(id);
        if (parkingEventOptional.isPresent()) {
            ParkingEvent parkingEvent = parkingEventOptional.get();
            if (!StringUtils.isEmpty(parkingEvent.getEventId())) {
                AnprEvent anprEvent = anprEventRepository.findByEventId(parkingEvent.getEventId());
                if (anprEvent != null) {
                    Long maskedAnprEventId = new DummyResponse().mask(anprEvent.getId());
                    resource = atccDataService.downloadVehicleImage(maskedAnprEventId);
                }
            }
        }
        if (resource == null) {
            throw new NotFoundException("File not found " + id);
        }
        return resource;
    }
}
