package io.synlabs.synvision.jpa;

import io.synlabs.synvision.entity.vids.VidsAlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VidsAlertSettingRepository extends JpaRepository<VidsAlertSetting, Long> {

}
