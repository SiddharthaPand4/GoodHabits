package io.synlabs.synvision.views.apms;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidentFilterRequest implements Request{

    public String checkIn;
    public String checkOut;


    public int page;
    public int pageSize;
}
