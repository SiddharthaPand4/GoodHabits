package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.core.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends JpaRepository<Org, Long> {

    Org findFirstByOrderById();

}
