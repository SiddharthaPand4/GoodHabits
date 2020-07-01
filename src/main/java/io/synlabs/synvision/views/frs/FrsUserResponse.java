package io.synlabs.synvision.views.frs;

import io.synlabs.synvision.entity.frs.RegisteredPerson;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FrsUserResponse implements Response {

    private String type;
    private String pid;
    private String name;
    private boolean active;

    public FrsUserResponse(RegisteredPerson rp) {
        this.type = rp.getPersonType().name();
        this.pid = rp.getPid();
        this.name = rp.getName();
        this.active = rp.isActive();
    }
}
