package io.synlabs.synvision.views.frs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.synlabs.synvision.entity.frs.RegisteredPerson;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Getter
@NoArgsConstructor
public class FRSLookupResponse {
    private String id;

    private String name;

    public FRSLookupResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public FRSLookupResponse(RegisteredPerson person) {

        if (person == null) {
            this.name = "UNKNOWN";
        }
        else {
            this.id = person.getPid();
            this.name = person.getName();
        }
    }

    public static FRSLookupResponse fromJson(String string) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(string, FRSLookupResponse.class);
    }
}
