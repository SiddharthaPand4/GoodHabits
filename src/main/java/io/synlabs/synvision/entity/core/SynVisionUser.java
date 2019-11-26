package io.synlabs.synvision.entity.core;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class SynVisionUser extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "firstname", nullable = false, length = 20)
    private String firstname;

    @Column(name = "lastname", length = 20)
    private String lastname;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private String externalId;

    public Set<String> getPrivileges()
    {
        Set<String> usrPrivileges = new HashSet<>();
        for (Role userRole : roles)
        {
            for (Privilege privilege : userRole.getPrivileges())
            {
                usrPrivileges.add(privilege.getName());
            }
        }
        return usrPrivileges;
    }

    public void addRole(Role role)
    {
        if (roles == null)
        {
            roles = new HashSet<>();
        }
        roles.add(role);
    }
}
