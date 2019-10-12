package io.synlabs.synvision.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role extends BaseEntity {


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_privilege",
            joinColumns = { @JoinColumn(name = "ROLE_ID") },
            inverseJoinColumns = { @JoinColumn(name = "PRIV_ID") })
    private Set<Privilege> privileges;


    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    @Column(length = 500)
    private String dashboard;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<SynVisionUser> users;

    public void addPrivilege(Privilege privilege)
    {
        if (privileges == null) privileges = new HashSet<>();
        privileges.add(privilege);
    }
}
