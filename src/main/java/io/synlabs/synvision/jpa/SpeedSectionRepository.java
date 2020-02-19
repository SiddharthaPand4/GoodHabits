package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.anpr.SpeedSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeedSectionRepository extends JpaRepository<SpeedSection, Long> {

    SpeedSection findOneByExitSite(String site);
}
