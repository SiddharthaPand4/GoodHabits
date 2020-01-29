package io.synlabs.synvision.views.frs;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FRSRegisterResponse {

    private String id;

    private String name;

    private String address;

    public FRSRegisterResponse(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

}
