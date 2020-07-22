package io.synlabs.synvision.views.common;

import io.synlabs.synvision.entity.core.Feed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedResponse implements Response {
    private Long id;
    private String url;
    private String name;
    private String location;
    private String site;


    public FeedResponse(Feed feed) {
        this.id = mask(feed.getId());
        this.url = feed.getUrl();
        this.name = feed.getName();
        this.location = feed.getLocation();
        this.site = feed.getSite();
    }
}


