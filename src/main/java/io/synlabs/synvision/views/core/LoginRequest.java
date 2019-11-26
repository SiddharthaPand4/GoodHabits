package io.synlabs.synvision.views.core;


import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest implements Request
{
    private String username;
    private String email;
    private String password;
}
