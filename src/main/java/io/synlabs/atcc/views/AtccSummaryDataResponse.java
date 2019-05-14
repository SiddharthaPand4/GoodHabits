package io.synlabs.atcc.views;

import io.synlabs.atcc.entity.AtccSummaryData;
import io.synlabs.atcc.enums.TimeSpan;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class AtccSummaryDataResponse {
    private Long id;
    private String type;
    private String date;
    private String from;
    private String to;
    private TimeSpan span;
    private int count;

    public AtccSummaryDataResponse(AtccSummaryData atccSummaryData) {
        this.id = atccSummaryData.getId();
        this.type = atccSummaryData.getType();
        this.date = new SimpleDateFormat("dd/MM/YYYY").format(atccSummaryData.getDate());
        this.from = new SimpleDateFormat("hh:mm:ss").format(atccSummaryData.getFrom());
        this.to = new SimpleDateFormat("hh:mm:ss").format(atccSummaryData.getTo());
        this.span = atccSummaryData.getSpan();
        this.count = atccSummaryData.getCount();
    }
}
