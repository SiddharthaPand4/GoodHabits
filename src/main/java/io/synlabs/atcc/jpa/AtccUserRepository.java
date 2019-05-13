package io.synlabs.atcc.jpa;


import io.synlabs.atcc.entity.AtccUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtccUserRepository extends JpaRepository<AtccUser, Long> {

    AtccUser findByEmailAndActiveTrue(String email);

    AtccUser findByEmail(String email);

}
