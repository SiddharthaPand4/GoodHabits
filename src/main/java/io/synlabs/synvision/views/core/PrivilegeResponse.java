package io.synlabs.synvision.views.core;

import io.synlabs.synvision.entity.core.Privilege;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

/**
 * Created by itrs on 10/16/2019.
 */
@Getter
public class PrivilegeResponse implements Response {
    private Long id;
    private String name;

    public PrivilegeResponse(Privilege privilege) {
        this.id = mask(privilege.getId());
        this.name = privilege.getName();
    }
}
