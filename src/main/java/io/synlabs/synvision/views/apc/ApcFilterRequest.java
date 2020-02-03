package io.synlabs.synvision.views.apc;

import io.synlabs.synvision.views.common.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApcFilterRequest extends PageRequest {
    public String eventId;
    public String fromDate;
    public String fromTime;
    public String toDate;
    public String toTime;

}
