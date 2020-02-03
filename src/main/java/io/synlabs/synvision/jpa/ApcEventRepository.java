package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.apc.ApcEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApcEventRepository extends JpaRepository<ApcEvent, Long> {

}
