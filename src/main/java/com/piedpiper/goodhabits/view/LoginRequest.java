package com.piedpiper.goodhabits.view;

import com.piedpiper.goodhabits.view.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest implements Request {

    private String email;
    private String username;
    private String password;

}
