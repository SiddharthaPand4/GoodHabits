package io.synlabs.synvision.controller;

import io.jsonwebtoken.io.IOException;
import io.synlabs.synvision.service.FeedService;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import io.synlabs.synvision.views.core.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
        return feedService.getFeeds().stream().map(FeedResponse::new).collect(Collectors.toList());
    }

    @GetMapping("{FeedId}")
    public FeedResponse getFeed(@PathVariable Long FeedId){
        return new FeedResponse(feedService.getFeed(new FeedRequest(FeedId)));
    }

    @DeleteMapping("{FeedId}")
    public void deleteFeed(@PathVariable Long FeedId)
    {
        feedService.deleteFeed(new FeedRequest(FeedId));
    }

    // Feed Streaming part

    @GetMapping("/start")
     public void startFeed(@RequestParam Long feedId) throws IOException, InterruptedException {
       feedService.startFeed(new FeedRequest(feedId));
     }

    @GetMapping("/stop")
    public void stopFeed() {
         feedService.stopFeed();
    }
}
