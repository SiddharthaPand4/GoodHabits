package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.parking.ParkingLot;
import io.synlabs.synvision.entity.parking.ParkingSlot;
import io.synlabs.synvision.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findAllByLotNameOrderByName(String lot);

    ParkingSlot findOneByNameAndLotName(String slot, String lot);


    long countByLot(ParkingLot lot);

    long countByLotAndFree(ParkingLot lot, boolean free);

    long countByLotAndFreeAndMisaligned(ParkingLot lot, boolean free,boolean misaligned);

    long countByLotAndVehicleType(ParkingLot lot, VehicleType type);

    long countByLotAndFreeAndVehicleType(ParkingLot lot, boolean free, VehicleType type);
}
