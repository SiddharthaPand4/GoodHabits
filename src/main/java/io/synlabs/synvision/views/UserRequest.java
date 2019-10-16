package io.synlabs.synvision.views;

import io.synlabs.synvision.entity.Org;
import io.synlabs.synvision.entity.SynVisionUser;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by itrs on 10/14/2019.
 */
@Getter
@Setter
public class UserRequest  implements  Request{
    public Long    id;
    public String  userName;
    public String  firstName;
    public String  lastName;
    public String  email;
    public boolean active=true;
    public List<String> roles   = new ArrayList<>();


    public UserRequest(Long id)
    {
        this.id = id;
    }

    public SynVisionUser toEntity(SynVisionUser user)
    {
        if (user == null)
        {
            user = new SynVisionUser();
        }
        user.setActive(this.active);
        user.setEmail(this.email);
        user.setFirstname(this.firstName);
        user.setLastname(this.lastName);
        user.setUsername(this.userName);
        //user.setPhone(this.phone);
        return user;
    }

    public UserRequest(){

    }

    public SynVisionUser toEntity()
    {
        return toEntity(new SynVisionUser());
    }

    public Long getId()
    {
        return unmask(id);
    }

    public void setId(Long id)
    {
        this.id = id;
    }
}
