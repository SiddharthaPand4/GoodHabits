package com.piedpiper.goodhabits.view;

import com.piedpiper.goodhabits.view.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse implements Response {

    private String authToken;

    public LoginResponse (String token) {
        this.authToken = token;
    }

}
