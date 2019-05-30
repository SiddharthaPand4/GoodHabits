package io.synlabs.atcc.jpa;

import io.synlabs.atcc.entity.AtccRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;

import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface AtccRawDataRepository extends JpaRepository<AtccRawData, Long> {

    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE))
    @Query(value = "SELECT d from AtccRawData d order by timeStamp desc")
    public Stream<AtccRawData> getAll();

}
