package io.synlabs.synvision.views.avc;

import io.synlabs.synvision.entity.avc.AvcEvent;
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
public class AvcEventRequest implements Request {


    private long timeStamp;

    private int lane;

    private int seek;

    private BigDecimal speed;

    private int direction;

    private String type;

    private String eventImage;

    private String eventVideo;

    private String source;

    private String vid;

    private Long surveyId;

    public Long getSurveyId() {
        return unmask(this.surveyId);
    }

    public AvcEvent toEntity() {
        AvcEvent avcEvent = new AvcEvent();
        avcEvent.setEventDate(new Date(timeStamp * 1000));
        avcEvent.setEventId(UUID.randomUUID().toString());
        avcEvent.setTimeStamp(timeStamp);
        avcEvent.setLane(lane);
        avcEvent.setSpeed(speed);
        avcEvent.setDirection(direction);
        avcEvent.setType(type);
        avcEvent.setEventImage(eventImage);
        avcEvent.setEventVideo(eventVideo);
        avcEvent.setSeek(seek);
        avcEvent.setVid(vid);
        return avcEvent;
    }
}
