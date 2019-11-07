package io.synlabs.synvision.views;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by itrs on 10/23/2019.
 */
@Getter
@Setter
public class DashboardRequest {
    public Date from;
    public Date to;
    public int month;
    public int year;
    public Date selectedDate;

    public String filterType;
}
