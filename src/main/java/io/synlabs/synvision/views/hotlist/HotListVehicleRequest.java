package io.synlabs.synvision.views.hotlist;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotListVehicleRequest implements Request {

    private Long id;
    private String lpr;
    private boolean archived = false;
}
