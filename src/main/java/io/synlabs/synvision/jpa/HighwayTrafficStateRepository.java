package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.entity.vids.HighwayTrafficState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighwayTrafficStateRepository extends JpaRepository<HighwayTrafficState, Long> {

    HighwayTrafficState findFirstByOrderByUpdateDateDesc();

    HighwayTrafficState findFirstByFeedOrderByUpdateDateDesc(Feed feed);
}
