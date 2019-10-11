package io.synlabs.atcc.jpa;

import io.synlabs.atcc.entity.atcc.AtccRawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtccRawDataRepository extends JpaRepository<AtccRawData, Long> {
}
