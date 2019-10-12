package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.atcc.AtccSummaryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtccSummaryDataRepository extends JpaRepository<AtccSummaryData, Long> {
}
