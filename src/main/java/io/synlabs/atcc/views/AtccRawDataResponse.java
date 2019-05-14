package io.synlabs.atcc.views;

import io.synlabs.atcc.entity.AtccRawData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class AtccRawDataResponse {

    private Long id;
    private String time;
    private String date;
    private long timeStamp;
    private int lane;
    private BigDecimal speed;
    private int direction;
    private String type;

    public AtccRawDataResponse(AtccRawData atccRawData) {
        this.id = atccRawData.getId();
        this.time = new SimpleDateFormat("hh:mm:ss").format(atccRawData.getTime());
        this.date = new SimpleDateFormat("dd/MM/YYYY").format(atccRawData.getDate());
        this.timeStamp = atccRawData.getTimeStamp();
        this.lane = atccRawData.getLane();
        this.speed = atccRawData.getSpeed();
        this.direction = atccRawData.getDirection();
        this.type = atccRawData.getType();
    }
}
