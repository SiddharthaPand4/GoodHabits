package io.synlabs.synvision.views.core;

import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.entity.core.Privilege;
import io.synlabs.synvision.entity.core.Role;
import io.synlabs.synvision.jpa.PrivilegeRepository;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.collection.internal.PersistentSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RoleRequest implements Request {
    private Long                   id;
    private String                 name;
    private List<String> privileges = new ArrayList<>();


    public RoleRequest(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return unmask(id);
    }
    public void setId(Long id)
    {
        this.id = id;
    }

    public Role toEntity(Role role) {
        if(role==null)
        {
            role=new Role();
        }
        role.setName(this.name);
        return role;
    }

   public Role toEntity()
    {
        return toEntity( new Role());
    }
}
