package io.synlabs.synvision.views.core;

import io.synlabs.synvision.entity.core.SynVisionUser;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by itrs on 10/14/2019.
 */
@Getter
public class UserResponse implements Response {
    private Long    id;
    private String  userName;
    private String firstName;
    private String lastName;
    private String  fullName;
    private String  phone;
    private String  email;
     private boolean active;
    private Set<String> roles   = new HashSet<>();


    public UserResponse(SynVisionUser user)
    {
        this.id = mask(user.getId());
        this.fullName = user.getFirstname().concat(user.getLastname());
        this.firstName=user.getFirstname();
        this.lastName=user.getLastname();
        this.userName=user.getUsername();
        this.email=user.getEmail();
        this.phone = user.getPhone();
        this.active = user.isActive();

        if (user.getRoles() != null)
        {
            user.getRoles().forEach(role -> {
                this.roles.add(role.getName());
            });
        }


    }
}
