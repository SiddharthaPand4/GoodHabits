package io.synlabs.synvision.views.anpr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.anpr.TrafficEvent;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateTrafficIncident implements Request {

    private Long id;

    @JsonProperty("vehicle_id")
    private String eventId;

    @JsonProperty("vehicle_image_name")
    private String vehicleImage;

    @JsonProperty("event_type")
    private String eventType;

    private long timestamp;

    public Long getId() {
        return unmask(id);
    }

    public TrafficEvent toEntity() {
        TrafficEvent event = new TrafficEvent();

        event.setEventDate(new Date(timestamp * 1000));
        event.setEventId(eventId);
        event.setVehicleImage(vehicleImage);
        event.setEventType(eventType);
        return event;
    }
}
