package io.synlabs.synvision.views;

import io.synlabs.synvision.entity.core.Org;
import io.synlabs.synvision.views.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrgResponse implements Response {
    private Long id;
    private String name;
    private String legalName;
    private String logoFileName;

    public void setId(Long id) {
        this.id = mask(id);
    }

    public OrgResponse(Org org) {
        this.id = mask(org.getId());
        this.name = org.getName();
        this.legalName = org.getLegalName();
        this.logoFileName = org.getLogoFileName();
    }
}
