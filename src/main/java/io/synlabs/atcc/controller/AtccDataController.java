package io.synlabs.atcc.controller;

import io.synlabs.atcc.entity.AtccRawData;
import io.synlabs.atcc.entity.AtccSummaryData;
import io.synlabs.atcc.service.AtccDataService;
import io.synlabs.atcc.view.AtccRawDataResponse;
import io.synlabs.atcc.view.AtccSummaryDataResponse;
import io.synlabs.atcc.view.ResponseWrapper;
import io.synlabs.atcc.view.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/data")
public class AtccDataController {

    @Autowired
    private AtccDataService dataService;

    @GetMapping("raw")
    public List<AtccRawDataResponse> findRawData() {
        return dataService.listRawData();
    }

    @PutMapping("summary")
    public ResponseWrapper<AtccSummaryDataResponse> findSummaryData(@RequestBody SearchRequest searchRequest) {
        System.out.println(searchRequest);
        return dataService.listSummaryData(searchRequest);
    }
}
