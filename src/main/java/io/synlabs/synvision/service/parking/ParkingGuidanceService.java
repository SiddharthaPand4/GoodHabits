package io.synlabs.synvision.service.parking;

import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.entity.parking.ParkingLot;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import io.synlabs.synvision.jpa.ParkingLotRepository;
import io.synlabs.synvision.jpa.ParkingSlotRepository;
import io.synlabs.synvision.views.parking.HourlyStatsResponse;
import io.synlabs.synvision.views.parking.ParkingDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ParkingGuidanceService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private ParkingEventRepository parkingEventRepository;

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

    //TODO stats by hour
    public List<HourlyStatsResponse> hourly(String lot) {
        return Collections.emptyList();
    }
}
