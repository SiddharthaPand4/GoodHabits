package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.FeedService;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import io.synlabs.synvision.views.core.UserRequest;
import io.synlabs.synvision.views.core.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed/")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @PostMapping
    public FeedResponse addFeed(@RequestBody FeedRequest request) {
        return new FeedResponse(feedService.addFeed(request));
    }

    @GetMapping("list")
    public List<FeedResponse> getFeeds() {
        return feedService.getFeeds();
    }



    @DeleteMapping
    public void deleteFeed(@RequestParam String url)
    {
        feedService.deleteFeed(url);
    }

}
