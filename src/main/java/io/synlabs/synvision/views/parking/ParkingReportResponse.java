package io.synlabs.synvision.views.parking;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by itrs on 02/03/2020.
 */
@Getter
@Setter
public class ParkingReportResponse {
    public Long totalCheckOut;
    public Long totalCheckIn;
    public String date;

    public ParkingReportResponse(Long checkIn, Long checkOut) {
        this.totalCheckOut = checkOut == null ? 0 : checkOut;
        this.totalCheckIn = checkIn == null ? 0 : checkIn;
    }

    public ParkingReportResponse(Long checkIn, Long checkOut,String date) {
        this.totalCheckOut = checkOut == null ? 0 : checkOut;
        this.totalCheckIn = checkIn == null ? 0 : checkIn;
        this.date=date;
    }
}
