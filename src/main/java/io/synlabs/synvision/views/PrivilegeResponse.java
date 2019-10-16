package io.synlabs.synvision.views;

import io.synlabs.synvision.entity.Privilege;
import lombok.Getter;

/**
 * Created by itrs on 10/16/2019.
 */
@Getter
public class PrivilegeResponse implements Response {
    private Long id;
    private String name;

    public PrivilegeResponse(Privilege privilege)
    {
        this.id = mask(privilege.getId());
        this.name = privilege.getName();
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

}
