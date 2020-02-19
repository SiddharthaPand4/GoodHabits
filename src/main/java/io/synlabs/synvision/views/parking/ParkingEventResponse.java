package io.synlabs.synvision.views.parking;

import io.synlabs.synvision.entity.parking.ParkingEvent;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by itrs on 02/04/2020.
 */
@Getter
public class ParkingEventResponse {

    private String vehicleNo;

    private String eventId;

    private String checkInDate;
    private String checkInTime;

    private String checkOutDate;
    private String checkOutTime;

    public ParkingEventResponse(ParkingEvent parkingEvent){
        this.vehicleNo= parkingEvent.getVehicleNo();
        this.eventId=parkingEvent.getEventId();
        this.checkInDate= new SimpleDateFormat("dd/MM/yyyy").format(parkingEvent.getCheckIn());
        this.checkInTime= new SimpleDateFormat("HH:mm:ss").format(parkingEvent.getCheckIn());
        this.checkOutDate= parkingEvent.getCheckOut()!=null ? new SimpleDateFormat("dd/MM/yyyy").format(parkingEvent.getCheckOut()) :"";
        this.checkOutTime= parkingEvent.getCheckOut()!=null ? new SimpleDateFormat("HH:mm:ss").format(parkingEvent.getCheckOut()):"";
    }
}
