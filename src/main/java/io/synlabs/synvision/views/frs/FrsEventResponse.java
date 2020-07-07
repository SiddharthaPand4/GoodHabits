package io.synlabs.synvision.views.frs;

import io.synlabs.synvision.entity.frs.FrsEvent;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class FrsEventResponse implements Response {
    private String eventId;
    private String type;
    private FrsUserResponse person;
    private String fullImage;
    private String faceImage;
    private boolean archived;
    private boolean alert;
    private boolean acknowledged;
    private String acknowledgedBy;
    private Date eventDate;

    public FrsEventResponse(FrsEvent event) {
        this.eventId = event.getEventId();
        this.type = event.getType().name();
        this.person = event.getPerson() == null ? null : new FrsUserResponse(event.getPerson());
        this.fullImage = event.getFullImage();
        this.faceImage = event.getFaceImage();
        this.archived = event.isArchived();
        this.alert = event.isAlert();
        this.acknowledged = event.isAcknowledged();
        this.acknowledgedBy = event.getAcknowledgedBy() == null ? null : event.getAcknowledgedBy().getUsername();
        this.eventDate = event.getEventDate();
    }
}
