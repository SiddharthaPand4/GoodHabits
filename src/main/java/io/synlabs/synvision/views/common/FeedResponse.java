package io.synlabs.synvision.views.common;

import io.synlabs.synvision.entity.core.Feed;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedResponse {
    private String name;
    private String location;
    private String site;

    public FeedResponse(Feed feed) {
        this.name = feed.getName();
        this.location = feed.getLocation();
        this.site = feed.getSite();
    }
}
