package io.synlabs.synvision.views.apc;

import io.synlabs.synvision.entity.apc.ApcEvent;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class ApcResponse implements  Response{
    private Long id;
    private String eventId;

    private Date eventDate;

    private boolean archived;

    private String direction;

    private String source;

    public ApcResponse(ApcEvent apcEvent){
        this.id=mask(apcEvent.getId());
        this.eventDate= apcEvent.getEventDate();
        this.eventId= apcEvent.getEventId();

        this.direction = apcEvent.getDirection();

        this.source=apcEvent.getSource();
        this.archived=apcEvent.isArchived();

    }
}

