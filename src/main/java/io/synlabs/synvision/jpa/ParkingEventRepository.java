package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.apms.ParkingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {


}
