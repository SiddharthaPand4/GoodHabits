package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.frs.RegisteredPerson;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RegisteredPersonRepository extends JpaRepository<RegisteredPerson, Long> {
    RegisteredPerson findOneByPidAndActiveTrue(String id);
}
