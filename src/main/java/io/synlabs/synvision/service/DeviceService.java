package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.core.Device;
import io.synlabs.synvision.jpa.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by itrs on 10/16/2019.
 */
@Service
public class DeviceService extends BaseService {

    @Autowired
    private DeviceRepository deviceRepository;

    public Device listDevices(){
        return deviceRepository.findAllByOrg(getAtccUser().getOrg());

    }
}
