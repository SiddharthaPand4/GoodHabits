package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.avc_.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
}
