package com.piedpiper.goodhabits.controller;

import com.piedpiper.goodhabits.service.UserService;
import com.piedpiper.goodhabits.view.signup.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public void createUser (@RequestBody CreateUserRequest request) {
        userService.createUser(request);
    }

}
