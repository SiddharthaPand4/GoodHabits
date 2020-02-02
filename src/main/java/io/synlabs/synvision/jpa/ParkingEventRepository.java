package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.parking.ParkingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {

    ParkingEvent findFirstByVehicleNoAndCheckOutOrderByIdDesc(String vehicleNo, Date checkOut);

}
