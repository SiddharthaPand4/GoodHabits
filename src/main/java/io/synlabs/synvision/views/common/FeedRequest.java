package io.synlabs.synvision.views.common;

import io.synlabs.synvision.entity.core.Feed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedRequest implements Request {
    public Long id;
    private String url;
    private String name;
    private String location;
    private String site;

    public FeedRequest(Long id) {
        this.id = id;
    }

    public FeedRequest() {
    }

    public Feed toEntity(Feed feed) {
        feed.setUrl(this.url);
        feed.setLocation(this.location);
        feed.setName(this.name);
        feed.setSite(this.site);
        return feed;
    }

    public Feed toEntity() {
        return toEntity(new Feed());
    }

    public Long getId() {
        return unmask(id);
    }
}

