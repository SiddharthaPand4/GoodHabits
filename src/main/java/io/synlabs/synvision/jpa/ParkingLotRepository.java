package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.parking.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    ParkingLot findOneByName(String lot);
}
