package io.synlabs.atcc.service;

import io.synlabs.atcc.entity.AtccRawData;
import io.synlabs.atcc.entity.AtccSummaryData;
import io.synlabs.atcc.jpa.AtccRawDataRepository;
import io.synlabs.atcc.jpa.AtccSummaryDataRepository;
import io.synlabs.atcc.view.AtccRawDataResponse;
import io.synlabs.atcc.view.AtccSummaryDataResponse;
import io.synlabs.atcc.view.ResponseWrapper;
import io.synlabs.atcc.view.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtccDataService {

    @Autowired
    private AtccRawDataRepository rawDataRepository;

    @Autowired
    private AtccSummaryDataRepository summaryDataRepository;


    public List<AtccRawDataResponse> listRawData() {
        List<AtccRawData> atccRawData = rawDataRepository.findAll();
        return atccRawData.stream().map(AtccRawDataResponse::new).collect(Collectors.toList());
    }

    public ResponseWrapper<AtccSummaryDataResponse> listSummaryData(SearchRequest searchRequest) {
        Page<AtccSummaryData> atccSummaryData = summaryDataRepository.findAll(PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize(), Sort.by(Sort.Direction.ASC, "type")));
        List<AtccSummaryDataResponse> collect = atccSummaryData.get().map(AtccSummaryDataResponse::new).collect(Collectors.toList());
        ResponseWrapper<AtccSummaryDataResponse> wrapper = new ResponseWrapper<>();
        wrapper.setData(collect);
        wrapper.setCurrPage(searchRequest.getPage());
        wrapper.setTotalElements(atccSummaryData.getTotalElements());
        return wrapper;
    }
}
