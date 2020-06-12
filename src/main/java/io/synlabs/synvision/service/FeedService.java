package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.core.Feed;

import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.FeedRepository;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService {

    private final FeedRepository feedRepository;

    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public Feed addFeed(FeedRequest request) {

        Feed feed = feedRepository.findByUrl(request.getUrl());
        if (feed != null) {
            throw new ValidationException(String.format("Already exists URL", request.getUrl()));

        }
        feed = request.toEntity();

        return feedRepository.save(feed);

    }

    public List<FeedResponse> getFeeds() {

        return feedRepository.findAll().stream().map(FeedResponse::new).collect(Collectors.toList());
    }


    public void deleteFeed(String url) {
       Feed feed = feedRepository.findByUrl(url);
             feedRepository.delete(feed);
    }


}
