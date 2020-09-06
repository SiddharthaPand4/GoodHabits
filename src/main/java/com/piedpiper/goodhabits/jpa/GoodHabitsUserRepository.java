package com.piedpiper.goodhabits.jpa;

import com.piedpiper.goodhabits.entity.GoodHabitsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodHabitsUserRepository extends JpaRepository<GoodHabitsUser, Long> {
    GoodHabitsUser findByUsernameAndActiveTrue(String username);

    GoodHabitsUser findByEmailAndActiveTrue(String email);

    GoodHabitsUser findByEmailOrUsername(String subject, String subject1);
}
