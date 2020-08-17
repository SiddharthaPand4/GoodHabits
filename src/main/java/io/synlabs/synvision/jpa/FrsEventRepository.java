package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.frs.FrsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface FrsEventRepository extends JpaRepository<FrsEvent, Long>, QuerydslPredicateExecutor<FrsEvent> {

    FrsEvent findOneByEventId(String eid);
}
