package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;

/**
 * Created by itrs on 10/21/2019.
 */
@Getter
@Setter
@NoArgsConstructor
public class AnprResponse implements Response {
    private Long id;
    private String eventId;

    private Date eventDate;

    private String ocrImage;

    private String anprText;
    private String vehicleImage;
    private String direction;
    private boolean helmet;
    private String location;

    public AnprResponse(AnprEvent anprEvent){
        this.id=mask(anprEvent.getId());
        this.eventDate= anprEvent.getEventDate();
        this.eventId= anprEvent.getEventId();
        this.vehicleImage= anprEvent.getVehicleImage();
        this.ocrImage= anprEvent.getOcrImage();
        this.anprText= anprEvent.getAnprText();
        this.direction = anprEvent.getDirection();
        this.helmet = anprEvent.isHelmetMissing();
        this.location = anprEvent.getSource();
    }
}
