package io.synlabs.synvision.views.frs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrsFilterRequest {
    public int page;
    public int pageSize;
    public String name;
}
