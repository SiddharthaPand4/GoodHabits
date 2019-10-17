package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.DeviceService;
import io.synlabs.synvision.views.DeviceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by itrs on 10/16/2019.
 */
@RestController
@RequestMapping("/api/device")
public class DeviceController  {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public DeviceResponse list(){
        return new DeviceResponse(deviceService.listDevices());
    }
}
