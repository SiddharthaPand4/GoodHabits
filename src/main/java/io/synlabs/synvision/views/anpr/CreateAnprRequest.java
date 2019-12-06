package io.synlabs.synvision.views.anpr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class CreateAnprRequest implements Request {

    private Long id;

    @JsonProperty("vehicle_id")
    private String eventId;
    private long timestamp;
    private String ocrImage;

    @JsonProperty("vehicle_plate_num")
    private String anprText;

    @JsonProperty("vehicle_image_name")
    private String vehicleImage;

    private String direction;

    @JsonProperty("vehicle_class")
    private String vehicleClass;

    @JsonProperty("helmet")
    private String helmetStatus;

    public Long getId() {
        return unmask(id);
    }

    public AnprEvent toEntity() {
        AnprEvent event = new AnprEvent();
        event.setAnprText(anprText);
        event.setEventDate(new Date(timestamp * 1000));
        event.setVehicleId(eventId);
        event.setOcrImage(ocrImage);
        event.setVehicleImage(vehicleImage);
        event.setDirection(direction);
        event.setVehicleClass(vehicleClass);
        event.setEventId(UUID.randomUUID().toString());
        if ("motorbike".equals(vehicleClass) && "None".equals(helmetStatus)) {
            event.setHelmetMissing(true);
        }

        return event;
    }
}
