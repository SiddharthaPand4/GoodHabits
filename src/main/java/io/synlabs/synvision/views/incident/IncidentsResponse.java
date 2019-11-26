package io.synlabs.synvision.views.incident;

import io.synlabs.synvision.entity.Incident;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

import java.util.Date;

/**
 * Created by itrs on 10/16/2019.
 */
@Getter
public class IncidentsResponse implements Response {

    private Long id;
    private String eventId;

    private String eventType;

    private String eventTrigger;

    private String eventDate;

    private Date eventStart;

    private Date eventEnd;

    private int eventDuration;

    private String videoId;

    private String imageId;

    public  IncidentsResponse(){

    }

    public  IncidentsResponse(Incident incident){
        this.id=mask(incident.getId());
        this.eventDate=incident.getEventDate();
        this.eventDuration=incident.getEventDuration();
        this.eventEnd=incident.getEventEnd();
        this.eventId=incident.getEventId();
        this.eventStart=incident.getEventStart();
        this.eventTrigger=incident.getEventTrigger();
        this.videoId=incident.getVideoId();
        this.imageId=incident.getImageId();
        this.eventType=incident.getEventType();

    }
}
