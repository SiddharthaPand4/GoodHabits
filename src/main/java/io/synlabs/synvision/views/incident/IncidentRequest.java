package io.synlabs.synvision.views.incident;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;

/**
 * Created by itrs on 10/18/2019.
 */
@Getter
public class IncidentRequest implements Request {
    public Long id;

    public IncidentRequest(Long id){
        this.id=id;
    }

    public IncidentRequest(){

    }

    public Long getId() {
        return unmask(id);
    }
}