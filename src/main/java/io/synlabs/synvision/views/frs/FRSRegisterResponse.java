package io.synlabs.synvision.views.frs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.synlabs.synvision.entity.frs.RegisteredPerson;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

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

    public FRSRegisterResponse(RegisteredPerson person) {
        this.id = person.getPid();
        this.name = person.getName();
        this.address = person.getAddress();
    }

}
