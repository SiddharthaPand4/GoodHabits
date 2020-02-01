package io.synlabs.synvision.service;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.apms.QParkingEvent;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.ImportStatusRepository;
import io.synlabs.synvision.views.apms.ApmsFilterRequest;
import io.synlabs.synvision.views.apms.ApmsPageResponse;
import io.synlabs.synvision.views.apms.ApmsResponse;
import io.synlabs.synvision.views.common.PageResponse;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.apms.ParkingEvent;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.persistence.EntityManager;
import java.nio.file.Path;
import java.util.*;

@Service
public class ApmsService {

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
}
