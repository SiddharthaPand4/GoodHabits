package io.synlabs.synvision.views.parking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApmsFilterRequest extends IncidentFilterRequest{
    private String vehicleNo;
}
