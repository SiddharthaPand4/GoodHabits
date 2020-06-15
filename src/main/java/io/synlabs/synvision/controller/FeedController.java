package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.FeedService;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
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

    @PutMapping
    public FeedResponse updateFeed(@RequestBody FeedRequest request)
    {return new FeedResponse(feedService.updateFeed(request));
    }

    @GetMapping("list")
    public List<FeedResponse> getFeeds() {
        return feedService.getFeeds();
    }

    @GetMapping("{url}")
    public FeedResponse getFeed(@PathVariable String url){
        return new FeedResponse(feedService.getFeed(url));
    }

    @DeleteMapping
    public void deleteFeed(@RequestParam String url)
    {
        feedService.deleteFeed(url);
    }

}
