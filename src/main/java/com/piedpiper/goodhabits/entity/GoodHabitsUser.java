package com.piedpiper.goodhabits.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GoodHabitsUser extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private Integer goodSamaritanPoints;

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

    @Transient
    public boolean isAdmin() {
        for (Role userRole : roles)
        {
            if ("admin".equalsIgnoreCase(userRole.getName())) return true;
        }
        return false;
    }

}
