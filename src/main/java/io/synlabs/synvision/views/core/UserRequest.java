package io.synlabs.synvision.views.core;

import io.synlabs.synvision.entity.core.SynVisionUser;
import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itrs on 10/14/2019.
 */
@Getter
@Setter
public class UserRequest implements Request {
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean active = true;
    private List<String> roles = new ArrayList<>();


    public UserRequest(Long id) {
        this.id = id;
    }

    public SynVisionUser toEntity(SynVisionUser user) {
        if (user == null) {
            user = new SynVisionUser();
        }
        user.setActive(this.active);
        user.setEmail(this.email);
        user.setFirstname(this.firstName);
        user.setLastname(this.lastName);
        user.setUsername(this.userName);

        if (this.password != null && !this.password.isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPasswordHash(encoder.encode(this.password));
        }
        //user.setPhone(this.phone);
        return user;
    }

    public UserRequest() {

    }

    public SynVisionUser toEntity() {
        return toEntity(new SynVisionUser());
    }

    public Long getId() {
        return unmask(id);
    }

    public void setId(Long id) {
        this.id = id;
    }
}
