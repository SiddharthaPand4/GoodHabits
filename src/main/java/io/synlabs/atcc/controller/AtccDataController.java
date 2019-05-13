package io.synlabs.atcc.controller;

import io.synlabs.atcc.entity.AtccRawData;
import io.synlabs.atcc.entity.AtccSummaryData;
import io.synlabs.atcc.service.AtccDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class AtccDataController {

    @Autowired
    private AtccDataService dataService;

    @GetMapping("raw")
    public List<AtccRawData> findRawData() {
        return dataService.listRawData();
    }

    @GetMapping("summary")
    public List<AtccSummaryData> findSummaryData() {
        return dataService.listSummaryData();
    }
}
