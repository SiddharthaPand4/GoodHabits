package io.synlabs.synvision.jpa;


import io.synlabs.synvision.entity.SynVisionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynVisionUserRepository extends JpaRepository<SynVisionUser, Long> {

    SynVisionUser findByEmailAndActiveTrue(String email);

    SynVisionUser findByEmail(String email);

    SynVisionUser findByUsernameAndActiveTrue(String username);

    SynVisionUser findByEmailOrUsername(String email, String username);
}
