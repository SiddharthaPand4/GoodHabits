package io.synlabs.synvision.views.incident;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by itrs on 10/16/2019.
 */
@Getter
@Setter
public class IncidentsFilterRequest implements Request {

        private String fromDate;
        private String fromTime;
        private String toDate;
        private String toTime;
        private String incidentType;

        private int page;
        private int pageSize;

        private Long feedId;

        public Long getFeedId() {
                return unmask(feedId);
        }

}
