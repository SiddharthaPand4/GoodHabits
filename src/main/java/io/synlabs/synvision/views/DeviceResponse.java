package io.synlabs.synvision.views;

import io.synlabs.synvision.entity.Device;
import lombok.Getter;

/**
 * Created by itrs on 10/16/2019.
 */
@Getter
public class DeviceResponse implements Response {
    private Long id;
    private String name;

    private String model;

    private String license;
    private String status;
    private String registeredTo;
    private String activeConfig;

    public DeviceResponse(Device device){
        this.id=mask(device.getId());
        this.name=device.getName();
        this.model=device.getModel();
        this.license=device.getLicense();
        this.status=device.getStatus();
        this.registeredTo=device.getRegisteredTo();
        this.activeConfig=device.getActiveConfig();
    }
}
