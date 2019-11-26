package io.synlabs.synvision.views.atcc;

import io.synlabs.synvision.entity.atcc.AtccSummaryData;
import io.synlabs.synvision.enums.TimeSpan;
import lombok.Getter;
import lombok.Setter;

import static java.time.temporal.TemporalAdjusters.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

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
        this.span = atccSummaryData.getSpan();
        this.count = atccSummaryData.getCount();
        this.date = new SimpleDateFormat("YYYY-MM-dd").format(atccSummaryData.getDate());

        LocalDate localDate = ((java.sql.Date) atccSummaryData.getDate()).toLocalDate();
        switch (atccSummaryData.getSpan()) {
            case Day:
                this.from = "00:00:00";
                this.to = "11:59:59";
                break;
            case Month:
                this.from = localDate.with(firstDayOfMonth()).toString();
                this.to = localDate.with(lastDayOfMonth()).toString();
                break;
            default:
                this.from = new SimpleDateFormat("HH:mm:ss").format(atccSummaryData.getFrom());
                this.to = new SimpleDateFormat("HH:mm:ss").format(atccSummaryData.getTo());
        }


    }
}
