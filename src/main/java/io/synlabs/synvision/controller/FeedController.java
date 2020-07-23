package io.synlabs.synvision.controller;

import io.jsonwebtoken.io.IOException;
import io.synlabs.synvision.service.FeedService;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import io.synlabs.synvision.auth.LicenseServerAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static io.synlabs.synvision.auth.LicenseServerAuth.Privileges.FEED_WRITE;

@RestController
@RequestMapping("/api/feed/")
@Secured(FEED_WRITE)
public class FeedController {

    @Autowired
    private FeedService feedService;

    // Feed Streaming part start /stop feed

    @GetMapping("/start")
    public int startFeed(@RequestParam Long feedId) throws IOException, InterruptedException {
        return feedService.startFeed(new FeedRequest(feedId));
    }

    @GetMapping("/stop")
    public void stopFeed(@RequestParam Long feedId) {
        feedService.stopFeed(feedId);
    }

    //feed operations add ,update,delete

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


}
