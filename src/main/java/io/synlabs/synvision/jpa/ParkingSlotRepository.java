package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.parking.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findAllByLotNameOrderByName(String lot);
    ParkingSlot findOneByNameAndLotName(String slot, String lot);
}
