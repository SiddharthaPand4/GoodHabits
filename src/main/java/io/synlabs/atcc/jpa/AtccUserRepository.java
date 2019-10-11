package io.synlabs.atcc.jpa;


import io.synlabs.atcc.entity.SynVisionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtccUserRepository extends JpaRepository<SynVisionUser, Long> {

    SynVisionUser findByEmailAndActiveTrue(String email);

    SynVisionUser findByEmail(String email);

}
