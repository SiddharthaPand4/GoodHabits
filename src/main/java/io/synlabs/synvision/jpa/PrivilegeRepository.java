package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.core.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivilegeRepository extends JpaRepository<Privilege,Long> {

    Privilege findByName(String name);
    Long countByName(String name);
    Privilege getOneByName(String name);

}
