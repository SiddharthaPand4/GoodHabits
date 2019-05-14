package io.synlabs.atcc.jpa;

import io.synlabs.atcc.entity.AtccSummaryData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtccSummaryDataRepository extends JpaRepository<AtccSummaryData, Long> {

}
