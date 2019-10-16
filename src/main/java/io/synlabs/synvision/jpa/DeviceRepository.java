package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.Device;
import io.synlabs.synvision.entity.Org;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by itrs on 10/16/2019.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findAllByOrg(Org org);
}
