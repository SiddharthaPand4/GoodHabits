package io.synlabs.atcc.jpa;

import io.synlabs.atcc.entity.atcc.AtccSummaryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtccSummaryDataRepository extends JpaRepository<AtccSummaryData, Long> {
}
