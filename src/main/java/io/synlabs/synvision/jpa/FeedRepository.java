package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.core.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long>  {
    Feed findOneByName(String name);

    Feed findByUrl(String url);

    void deleteByUrl(String url);
}
