package io.synlabs.synvision.views;


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
