package io.synlabs.synvision.views;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

}
