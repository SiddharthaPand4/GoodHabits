package io.synlabs.atcc.jpa;

import io.synlabs.atcc.entity.AtccVideoData;
import io.synlabs.atcc.views.VideoSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AtccVideoDataRepository extends JpaRepository<AtccVideoData, Long> {

    @Query(value = "SELECT id, filename, time_stamp as timestamp, offset FROM atcc_video_data where (time_stamp + offset) <= ?1 and (time_stamp + 120 + offset) > ?1 and feed = ?2 limit 1",nativeQuery = true)
    VideoSummary getAssociatedVideo(long ts, String feed);

    Optional<AtccVideoData> findOneByTimeStamp(long ts);
}
