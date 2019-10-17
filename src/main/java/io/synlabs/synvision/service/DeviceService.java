package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.Device;
import io.synlabs.synvision.jpa.DeviceRepository;
import io.synlabs.synvision.views.DeviceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
