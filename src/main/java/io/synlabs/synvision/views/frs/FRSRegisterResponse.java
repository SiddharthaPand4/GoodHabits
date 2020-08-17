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


    public FRSRegisterResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public FRSRegisterResponse(RegisteredPerson person) {
        this.id = person.getPid();
        this.name = person.getName();
    }

}
