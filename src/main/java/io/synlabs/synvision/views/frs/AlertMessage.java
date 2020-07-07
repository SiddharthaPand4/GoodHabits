package io.synlabs.synvision.views.frs;

import io.synlabs.synvision.entity.frs.FrsEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlertMessage {
    private String accessType;
    private String message;
    private String uid;
    private String fullImage;
    private String faceImage;
    private FrsUserResponse person;
    private String type;

    public AlertMessage(FrsEvent event) {
        this.uid = event.getEventId();
        this.message = "Alert!";
        this.fullImage = event.getFullImage();
        this.faceImage = event.getFaceImage();

        if (event.getPerson() != null) {
            this.person = new FrsUserResponse(event.getPerson());
            this.type = event.getPerson().getPersonType().name();
            this.accessType = event.getPerson().getAccessType().name();
        }

    }
}
