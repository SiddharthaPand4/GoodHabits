package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.ConfigService;
import io.synlabs.synvision.views.ConfigRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/save")
public class ConfigController {


    @Autowired
    private ConfigService configService;

    @PostMapping("/annotation")
    // public void saveAnnotation(@RequestParam AnnotationRequest request) throws IOException {
    public void saveConfig(@RequestBody ConfigRequest request) throws IOException {
        configService.saveAnnotation(request);
    }



}
