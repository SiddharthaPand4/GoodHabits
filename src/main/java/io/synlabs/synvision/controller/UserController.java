package io.synlabs.synvision.controller;

import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.service.UserService;
import io.synlabs.synvision.views.RoleResponse;
import io.synlabs.synvision.views.UserRequest;
import io.synlabs.synvision.views.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by itrs on 10/14/2019.
 */
@RestController
@RequestMapping("/api/user/")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public List<UserResponse> listUsers(){
       return userService.listUsers();
    }

    @GetMapping("{userId}")
    public UserResponse list(@PathVariable(name = "userId") Long userId)
    {
        return new UserResponse(userService.getUserDetail(new UserRequest(userId)));
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request)
    {

        return new UserResponse(userService.createUser(request));
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody UserRequest request)
    {

        return new UserResponse(userService.updateUser(request));
    }


    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable Long userId)
    {
        userService.deleteUser(new UserRequest(userId));
    }


    @GetMapping("roles")
    public List<RoleResponse> roles()
    {
        return userService.getRoles().stream().map(RoleResponse::new).collect(Collectors.toList());
    }

}
