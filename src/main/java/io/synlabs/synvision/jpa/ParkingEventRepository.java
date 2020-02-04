package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {

    ParkingEvent findFirstByVehicleNoAndCheckOutOrderByIdDesc(String vehicleNo, Date checkOut);

    ParkingEvent findByEventIdAndCheckInIsNull(String eventId);
    long countAllByCheckOutIsNullAndAndType(VehicleType type);

}
