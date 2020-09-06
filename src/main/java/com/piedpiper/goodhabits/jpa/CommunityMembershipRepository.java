package com.piedpiper.goodhabits.jpa;

import com.piedpiper.goodhabits.entity.CommunityMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityMembershipRepository extends JpaRepository<CommunityMembership, Long> {
}
