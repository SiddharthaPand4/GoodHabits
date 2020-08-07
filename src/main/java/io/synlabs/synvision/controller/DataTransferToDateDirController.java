package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.DataTransferToDateDirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataTransferToDateDirController {

    @Autowired
    DataTransferToDateDirService dataTransferToDateDirService;
    @GetMapping("/transfer")
    public void TransferData(){
       dataTransferToDateDirService.transferAnprData();
       dataTransferToDateDirService.transferAtccData();
       dataTransferToDateDirService.transferVidsData();
       dataTransferToDateDirService.transferOffenceData();
       dataTransferToDateDirService.transferFlowStateData();
    }

}
