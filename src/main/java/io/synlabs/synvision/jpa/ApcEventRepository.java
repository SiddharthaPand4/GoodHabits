package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.apc.ApcEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ApcEventRepository extends JpaRepository<ApcEvent, Long> {


}
