package com.piedpiper.goodhabits.service;

import com.piedpiper.goodhabits.entity.Community;
import com.piedpiper.goodhabits.entity.CommunityMembership;
import com.piedpiper.goodhabits.entity.GoodHabitsUser;
import com.piedpiper.goodhabits.jpa.CommunityMembershipRepository;
import com.piedpiper.goodhabits.jpa.CommunityRepository;
import com.piedpiper.goodhabits.view.community.CreateCommunityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunityService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(CommunityService.class);

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityMembershipRepository membershipRepository;

    public void createCommunity(CreateCommunityRequest request) {
        Community community = new Community(request);
        GoodHabitsUser user = getCurrentUser();
        community.setAdmin(user);
        communityRepository.saveAndFlush(community);
        membershipRepository.saveAndFlush(new CommunityMembership(community, user));
    }
}
