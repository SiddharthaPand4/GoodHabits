package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.avc.AvcEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvcEventRepository extends JpaRepository<AvcEvent, Long> {
}
