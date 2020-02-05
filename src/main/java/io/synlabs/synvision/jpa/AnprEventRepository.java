package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.anpr.AnprEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Date;
import java.util.List;

/**
 * Created by itrs on 10/21/2019.
 */
public interface AnprEventRepository extends JpaRepository<AnprEvent, Long>, QuerydslPredicateExecutor<AnprEvent> {

    List<AnprEvent> findAllByEventDateBetweenAndArchivedFalse(Date eventStartDate, Date eventEndDate, Pageable paging);

    int countAllByEventDateBetweenAndArchivedFalse(Date eventStartDate, Date eventEndDate);

    int countAllByEventDateAndArchivedFalse(Date from);

    int countAllByArchivedFalse();

    List<AnprEvent> findAllByArchivedFalse(Pageable paging);

    @Query(nativeQuery = true, value = "SELECT count(*) FROM (SELECT anpr_text, count(anpr_text) as count FROM anpr_event where direction = 'rev' group by anpr_text having count > 1) as x ")
    long countReverseDirectionRepeatedIncidents();

    @Query(nativeQuery = true, value = "SELECT count(*) FROM (SELECT anpr_text, count(anpr_text) as count FROM anpr_event where helmet_missing = b'1' group by anpr_text having count > 1) as x  ")
    long countHelmetMissingRepeatedIncidents();

    @Query(nativeQuery = true, value = "SELECT count(*) FROM (SELECT anpr_text, count(anpr_text) as count FROM anpr_event  group by anpr_text) as x  ")
    long findTotalEventsCountListOfEachLpr();

    List<AnprEvent> findAllByAnprTextAndArchived(String anprText, boolean isArchived);

    AnprEvent findByEventId(String eventId);
}
