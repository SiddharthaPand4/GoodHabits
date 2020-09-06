package com.piedpiper.goodhabits.controller;

import com.piedpiper.goodhabits.service.CommunityService;
import com.piedpiper.goodhabits.view.community.CommunityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping("/")
    public void createCommunity(@RequestBody CommunityRequest request) {
        communityService.createCommunity(request);
    }

}
