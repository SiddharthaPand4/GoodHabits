package io.synlabs.synvision.views;

import lombok.Getter;

/**
 * Created by itrs on 10/23/2019.
 */
@Getter
public class DashboardResponse implements Response{

    private String key;
    private int countOfTotalVehicles;

    public DashboardResponse(String key, int value){
        this.key=key;
        this.countOfTotalVehicles=value;
    }
}
