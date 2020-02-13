package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AnprReportResponse implements Response {

    private Long id;
    private Date eventDate;
    private String lpr;
    private String direction;

    public AnprReportResponse(AnprEvent anprEvent)
    {
        this.id=mask(anprEvent.getId());
        this.eventDate= (anprEvent.getEventDate());
        this.lpr=anprEvent.getAnprText();
        this.direction=anprEvent.getDirection();

    }
}
