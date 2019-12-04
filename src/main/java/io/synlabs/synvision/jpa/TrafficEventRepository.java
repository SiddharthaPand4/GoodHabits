package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.anpr.TrafficEvent;
import io.synlabs.synvision.entity.core.Org;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by itrs on 10/21/2019.
 */
public interface TrafficEventRepository extends JpaRepository<TrafficEvent, Long> {


}
