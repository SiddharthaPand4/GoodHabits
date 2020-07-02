package io.synlabs.synvision.views.atcc;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtccRequest implements Request {
    public Long id;

    public AtccRequest(Long id){
        this.id=id;
    }
}
