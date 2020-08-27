package io.synlabs.synvision.views;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class OrgRequest implements Request {
    private Long id;
    private String name;
    private String legalName;
    private String logoFileName;

    public OrgRequest(Long id, String name, String legalName, String logoFileName) {
        this.id = id;
        this.name = name;
        this.legalName = legalName;
        this.logoFileName = logoFileName;
    }

    public Long getId() {
        return unmask(this.id);
    }
}
