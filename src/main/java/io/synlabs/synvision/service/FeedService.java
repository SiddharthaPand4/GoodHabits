package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.core.Feed;

import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.FeedRepository;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
            throw new ValidationException(String.format("Feed ID already exists", request.getUrl()));

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

    public Feed getFeed(String url) {
        return feedRepository.findByUrl(url);

    }

    public Feed updateFeed(FeedRequest request) {
        validateFeed(request);
        Feed feed=feedRepository.findByUrl(request.getUrl());
        request.toEntity(feed);
        return feedRepository.save(feed);

    }

    private void validateFeed(FeedRequest request)
    {
        if (StringUtils.isEmpty(request.getUrl()))
        {
            throw new ValidationException("Url is required");
        }


        if (StringUtils.isEmpty(request.getLocation()))
        {
            throw new ValidationException("Location is required.");
        }

        if (StringUtils.isEmpty(request.getName()))
        {
            throw new ValidationException("Name is required.");
        }

        if (StringUtils.isEmpty(request.getSite()))
        {
            throw new ValidationException("Site is required.");
        }
    }

}
