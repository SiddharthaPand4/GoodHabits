package io.synlabs.synvision.views.core;

import io.synlabs.synvision.entity.core.Role;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by itrs on 10/16/2019.
 */
@Getter
public class RoleResponse implements Response {
    private Long                   id;
    private String                 name;
    private Set<String> privileges = new HashSet<>();

    public RoleResponse(Role role)
    {

        this.id = mask(role.getId());
        this.name = role.getName();
         if (role.getPrivileges() != null)
        {
            role.getPrivileges().forEach(privilege -> this.privileges.add(privilege.getName()));
        }
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

   // public Set<PrivilegeResponse> getPrivileges()
   // {
   //     return privileges;
   // }
}//
