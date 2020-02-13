package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.core.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByEnabledTrue();
}
