package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.core.Device;
import io.synlabs.synvision.entity.core.Org;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by itrs on 10/16/2019.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findAllByOrg(Org org);
}
