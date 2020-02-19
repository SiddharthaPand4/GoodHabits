package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.vids.HighwayIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface HighwayIncidentRepository extends JpaRepository<HighwayIncident, Long>, QuerydslPredicateExecutor<HighwayIncident> {
}
