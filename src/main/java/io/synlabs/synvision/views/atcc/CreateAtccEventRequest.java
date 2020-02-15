package io.synlabs.synvision.views.atcc;

import io.synlabs.synvision.entity.atcc.AtccEvent;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateAtccEventRequest implements Request {


    private long timeStamp;

    private int lane;

    private BigDecimal speed;

    private int direction;

    private String type;

    private String eventImage;

    private String eventVideo;

    private String source;

    public AtccEvent toEntity() {
        AtccEvent atccEvent = new AtccEvent();
        atccEvent.setEventDate(new Date(timeStamp * 1000));
        atccEvent.setEventId(UUID.randomUUID().toString());
        atccEvent.setTimeStamp(timeStamp);
        atccEvent.setLane(lane);
        atccEvent.setSpeed(speed);
        atccEvent.setDirection(direction);
        atccEvent.setType(type);
        atccEvent.setEventImage(eventImage);
        atccEvent.setEventVideo(eventVideo);
        return atccEvent;
    }
}
