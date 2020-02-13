package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.atcc.AtccEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;

import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface AtccEventRepository extends JpaRepository<AtccEvent, Long> {

    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE))
    @Query(value = "SELECT d from AtccEvent d order by timeStamp desc")
    public Stream<AtccEvent> getAll();

}
