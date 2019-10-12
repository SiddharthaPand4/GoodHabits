package io.synlabs.synvision.views;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class LoginResponse implements Response
{

    private String token;
    private Set<String> privileges = new HashSet<>();
    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, Set<String> privileges)
    {
        this.token = token;
        this.privileges = privileges;
    }
}
