package com.piedpiper.goodhabits.view.signup;

import com.piedpiper.goodhabits.view.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest implements Request {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String userName;

}
