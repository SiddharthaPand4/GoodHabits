package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.AnnotationService;
import io.synlabs.synvision.service.DashboardService;
import io.synlabs.synvision.views.AnnotationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/save")
public class AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    @PostMapping("/annotation")
   // public void saveAnnotation(@RequestParam AnnotationRequest request) throws IOException {
    public void saveAnnotation(@RequestBody AnnotationRequest request) throws IOException {
       annotationService.saveAnnotation(request);
    }

    @GetMapping("/start")
    public void startFeed(@RequestParam String feedUrl) throws IOException, InterruptedException {
        annotationService.startFeed(feedUrl);
    }

    @GetMapping("/stop/feed")
    public void stopFeed() throws IOException, InterruptedException {
        annotationService.stopFeed();
    }
}
