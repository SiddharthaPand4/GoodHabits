package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.Incident;
import io.synlabs.synvision.entity.Org;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by itrs on 10/16/2019.
 */
public interface IncidentsRepository extends JpaRepository<Incident, Long> {
    List<Incident> findAllByOrg(Org org);

    List<Incident> findAllByOrgAndEventStartBetween(Org org, Date eventStartDate, Date eventEndDate);
}
