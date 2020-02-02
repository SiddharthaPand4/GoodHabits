package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.parking.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSlotRepository extends JpaRepository<ParkingLot, Long> {



}
