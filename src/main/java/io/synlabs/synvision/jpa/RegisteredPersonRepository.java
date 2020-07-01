package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.frs.RegisteredPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface RegisteredPersonRepository extends JpaRepository<RegisteredPerson, Long>, QuerydslPredicateExecutor<RegisteredPerson> {
    RegisteredPerson findOneByPidAndActiveTrue(String id);
}
