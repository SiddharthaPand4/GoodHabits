package io.synlabs.atcc.service;

import io.synlabs.atcc.entity.AtccRawData;
import io.synlabs.atcc.entity.AtccSummaryData;
import io.synlabs.atcc.jpa.AtccRawDataRepository;
import io.synlabs.atcc.jpa.AtccSummaryDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtccDataService {

    @Autowired
    private AtccRawDataRepository rawDataRepository;

    @Autowired
    private AtccSummaryDataRepository summaryDataRepository;


    public List<AtccRawData> listRawData() {
        return rawDataRepository.findAll();
    }

    public List<AtccSummaryData> listSummaryData() {
        return summaryDataRepository.findAll();
    }
}
