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
    public Long checkOut;
    public Long checkIn;

    public ParkingReportResponse(Long checkIn, Long checkOut) {
        this.checkOut = checkOut == null ? 0 : checkOut;
        this.checkIn = checkIn == null ? 0 : checkIn;
    }
}
