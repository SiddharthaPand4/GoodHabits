package io.synlabs.synvision.views.atcc;

import io.synlabs.synvision.entity.atcc.AtccEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class AtccRawDataResponse {

    private Long id;

    private Date eventDate;
    private long timeStamp;
    private int lane;
    private BigDecimal speed;
    private int direction;
    private String type;
    private String location;
    private String vehicleImage;

    public AtccRawDataResponse(AtccEvent atccEvent) {
        this.id = atccEvent.getId();
        this.eventDate = atccEvent.getEventDate();
        this.timeStamp = atccEvent.getTimeStamp();
        this.lane = atccEvent.getLane();
        this.speed = atccEvent.getSpeed();
        this.direction = atccEvent.getDirection();
        this.type = atccEvent.getType();
        this.location = atccEvent.getFeed().getLocation();
        this.vehicleImage = atccEvent.getEventImage();
    }
}
