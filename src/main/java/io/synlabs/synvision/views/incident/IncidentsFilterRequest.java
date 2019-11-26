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

        public String fromDate;
        public String fromTime;
        public String toDate;
        public String toTime;

        public int page;
        public int pageSize;

}
