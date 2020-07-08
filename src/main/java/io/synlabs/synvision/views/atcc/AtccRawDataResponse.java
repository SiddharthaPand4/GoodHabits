package io.synlabs.synvision.views.atcc;

import io.synlabs.synvision.entity.atcc.AtccEvent;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class AtccRawDataResponse implements Response {

    private Long id;
    private Date eventDate;
    private long timeStamp;
    private int lane;
    private BigDecimal speed;
    private int direction;
    private int seek;
    private String type;
    private String location;
    private String vehicleImage;
    private String vid;

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
        this.seek = atccEvent.getSeek();
        this.vid = atccEvent.getVid();
    }
}
