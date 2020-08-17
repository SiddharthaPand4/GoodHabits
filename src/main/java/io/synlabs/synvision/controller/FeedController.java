package io.synlabs.synvision.controller;

import io.jsonwebtoken.io.IOException;
import io.synlabs.synvision.service.FeedService;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.*;

@RestController
@RequestMapping("/api/feed/")
public class FeedController {

    @Autowired
    private FeedService feedService;

    // Feed Streaming part start /stop feed

    @GetMapping("/start")
    @Secured(FEED_READ)
    public int startFeed(@RequestParam Long feedId) throws IOException, InterruptedException {
        return feedService.startFeed(new FeedRequest(feedId));
    }

    @GetMapping("/stop")
    @Secured(FEED_READ)
    public void stopFeed(@RequestParam Long feedId) {
        feedService.stopFeed(feedId);
    }

    @PostMapping
    @Secured(FEED_WRITE)
    public FeedResponse addFeed(@RequestBody FeedRequest request) {
        return new FeedResponse(feedService.addFeed(request));
    }

    @PutMapping
    @Secured(FEED_WRITE)
    public FeedResponse updateFeed(@RequestBody FeedRequest request) {
        return new FeedResponse(feedService.updateFeed(request));
    }

    @GetMapping("list")
    @Secured(FEED_READ)
    public List<FeedResponse> getFeeds() {
        return feedService.getFeeds().stream().map(FeedResponse::new).collect(Collectors.toList());
    }

    @GetMapping("{FeedId}")
    @Secured(FEED_READ)
    public FeedResponse getFeed(@PathVariable Long FeedId) {
        return new FeedResponse(feedService.getFeed(new FeedRequest(FeedId)));
    }

    @DeleteMapping("{FeedId}")
    @Secured(FEED_WRITE)
    public void deleteFeed(@PathVariable Long FeedId) {
        feedService.deleteFeed(new FeedRequest(FeedId));
    }


}
