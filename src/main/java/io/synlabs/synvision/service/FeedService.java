package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.core.Feed;

import io.synlabs.synvision.ex.FeedStreamException;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.FeedRepository;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedService {
    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    private Process process;

    private final FeedRepository feedRepository;

    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public Feed addFeed(FeedRequest request) {
        validateFeed(request);
        Feed feed = feedRepository.findByUrl(request.getUrl());
        if (feed != null) {
            throw new ValidationException(String.format("Feed ID already exists", request.getUrl()));

        }
        feed = request.toEntity();
        return feedRepository.save(feed);

    }

    public List<Feed> getFeeds() {

        return feedRepository.findAll();
    }


    public void deleteFeed(FeedRequest request) {
        Feed feed = feedRepository.getOne(request.getId());
        feedRepository.delete(feed);
    }

    public Feed getFeed(FeedRequest request) {
        return feedRepository.getOne(request.getId());

    }

    public Feed updateFeed(FeedRequest request) {
        validateFeed(request);
        Feed feed = feedRepository.findByUrl(request.getUrl());
        request.toEntity(feed);
        return feedRepository.save(feed);

    }

    private void validateFeed(FeedRequest request) {
        if (StringUtils.isEmpty(request.getUrl())) {
            throw new ValidationException("Url is required");
        }


        if (StringUtils.isEmpty(request.getLocation())) {
            throw new ValidationException("Location is required.");
        }

        if (StringUtils.isEmpty(request.getName())) {
            throw new ValidationException("Name is required.");
        }

        if (StringUtils.isEmpty(request.getSite())) {
            throw new ValidationException("Site is required.");
        }
    }
//Feed streaming part

    public void startFeed(FeedRequest request) {
        File dir = new File("E://LiveFeed");
        String killCommand = "kill -9 $(lsof -t -i:9000)";
        if (SystemUtils.IS_OS_LINUX) {
            try {
                Runtime.getRuntime().exec(killCommand);
            } catch (IOException e) {
                logger.info("Couldn't kill the running process by executing cmd => " + killCommand);
            }
        }
        //For Windows we have to look for alternate code or We can do it manually
        // cmd > netstat -ano | find "9000" - this will return PID
        // taskkill /f /pid PID

        Feed feed = feedRepository.getOne(request.getId());
        String StreamCmd="streamer " + feed.getUrl() + " localhost:9000 ";
        try {
            process = Runtime.getRuntime().exec(StreamCmd, null, dir);
           // System.out.println(StreamCmd);
        } catch (IOException e) {
            throw new FeedStreamException("Couldn't start streaming from feed by command =>" + StreamCmd);
        }

    }

    public void stopFeed() {

        process.destroy();
    }

}
