package io.synlabs.atcc.jpa;

import io.synlabs.atcc.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getOneByName(String name);
}