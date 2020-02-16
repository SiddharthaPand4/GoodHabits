package io.synlabs.synvision.views;

import io.synlabs.synvision.views.common.Response;
import lombok.Getter;

/**
 * Created by itrs on 10/23/2019.
 */
@Getter
public class DashboardResponse implements Response {

    private String key;
    private int count;

    public DashboardResponse(String key, int value) {
        this.key = key;
        this.count = value;
    }

}
