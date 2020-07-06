package io.synlabs.synvision.views.vids;

import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import io.synlabs.synvision.views.incident.IncidentsFilterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VidsFilterRequest extends IncidentsFilterRequest {
    private String incidentType;
    private FeedRequest feed;
    private Long feedId;

    public Long getFeedId()
    {
        return unmask(feedId);
    }
}
