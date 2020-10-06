package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.avc.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Survey findFirstByFolderName(String folderName);
}
