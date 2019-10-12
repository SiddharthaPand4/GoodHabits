package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, Long> {
    long countByFilenameAndFeedAndStatus(String fileName, String tag, String status);
}
