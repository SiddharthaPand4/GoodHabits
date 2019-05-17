package io.synlabs.atcc.jpa;

import io.synlabs.atcc.entity.AtccVideoData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtccVideoDataRepository extends JpaRepository<AtccVideoData, Long> {
}
