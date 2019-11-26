package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.entity.anpr.Anpr;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

import java.util.Date;

/**
 * Created by itrs on 10/21/2019.
 */
@Getter
public class AnprResponse implements Response {
    private Long id;
    private String eventId;

    private Date eventDate;

    private String ocrImage;

    private String anprText;
    private String vehicleImage;

    public  AnprResponse(Anpr anpr){
        this.id=mask(anpr.getId());
        this.eventDate=anpr.getEventDate();
        this.eventId=anpr.getEventId();
        this.vehicleImage=anpr.getVehicleImage();
        this.ocrImage=anpr.getOcrImage();
        this.anprText=anpr.getAnprText();


    }
}
