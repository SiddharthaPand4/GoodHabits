package io.synlabs.atcc.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role extends BaseEntity {

    @Column(name = "uniq")
    private boolean unique;

    private boolean internal;

    @Column(length = 500)
    private String dashboard;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<SynVisionUser> users;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
}
