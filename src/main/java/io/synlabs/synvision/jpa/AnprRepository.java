package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.Anpr;
import io.synlabs.synvision.entity.Incident;
import io.synlabs.synvision.entity.Org;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by itrs on 10/21/2019.
 */
public interface AnprRepository  extends JpaRepository<Anpr, Long> {

    List<Anpr> findAllByOrg(Org org, Pageable paging);

    List<Anpr> findAllByOrgAndEventDateBetween(Org org, Date eventStartDate, Date eventEndDate, Pageable paging);

    int countAllByOrg(Org org);

    int countAllByOrgAndEventDateBetween(Org org, Date eventStartDate, Date eventEndDate);

    int countAllByOrgAndEventDate(Org org, Date from);
}
