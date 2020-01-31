package io.synlabs.synvision.views.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest implements Request {
    private int pageSize;
    private int page;
}
