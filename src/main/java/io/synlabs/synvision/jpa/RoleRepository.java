package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getOneByName(String name);
}