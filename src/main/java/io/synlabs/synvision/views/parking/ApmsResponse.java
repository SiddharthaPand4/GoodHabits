package io.synlabs.synvision.views.parking;

import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ApmsResponse implements Response{

    private Long id;
    private String vehicleNo;
    private String eventId;
    private Date checkIn;
    private Date checkOut;
    private boolean archived;
    private String source;
    private String location;

    public ApmsResponse(ParkingEvent parkingEvent)
    {
        this.id=mask(parkingEvent.getId());
        this.checkIn=parkingEvent.getCheckIn();
        this.checkOut=parkingEvent.getCheckOut();
        this.eventId=parkingEvent.getEventId();
        this.vehicleNo=parkingEvent.getVehicleNo();
        this.source=parkingEvent.getSource();
        this.location = location;
    }
}
