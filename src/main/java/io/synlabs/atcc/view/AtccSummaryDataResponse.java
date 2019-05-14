package io.synlabs.atcc.view;

import io.synlabs.atcc.entity.AtccSummaryData;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class AtccSummaryDataResponse {
    private String type;
    private String ts;
    private int count;

    public AtccSummaryDataResponse(AtccSummaryData atccSummaryData) {
        this.type = atccSummaryData.getType();
        this.ts = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss").format(atccSummaryData.getTs());
        this.count = atccSummaryData.getCount();
    }
}
