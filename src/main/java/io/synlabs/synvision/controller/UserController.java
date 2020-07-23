package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.UserService;
import io.synlabs.synvision.views.common.FeedRequest;
import io.synlabs.synvision.views.common.FeedResponse;
import io.synlabs.synvision.views.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static io.synlabs.synvision.auth.LicenseServerAuth.Privileges.*;

/**
 * Created by itrs on 10/14/2019.
 */
@RestController
@RequestMapping("/api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/menu")
    //@Secured(SELF_READ)
    public Menu getMenu() {

        return userService.getCurrentUserMenu();
    }

    @GetMapping
    @Secured(USER_WRITE)
    public List<UserResponse> listUsers() {
        return userService.listUsers();
    }

    @GetMapping("{userId}")
    public UserResponse list(@PathVariable(name = "userId") Long userId) {
        return new UserResponse(userService.getUserDetail(new UserRequest(userId)));
    }

    @PostMapping

    @Secured(USER_WRITE)
    public UserResponse createUser(@RequestBody UserRequest request) {

        return new UserResponse(userService.createUser(request));
    }

    @PutMapping
    @Secured(USER_WRITE)
    public UserResponse updateUser(@RequestBody UserRequest request) {

        return new UserResponse(userService.updateUser(request));
    }

    @DeleteMapping("{userId}")
    @Secured(USER_WRITE)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(new UserRequest(userId));
    }


    // Role Part Below. (All above APIs for USER)


    @GetMapping("/get/roles")
    @Secured(ROLE_WRITE)
    public List<RoleResponse> roles() {
        return userService.getRoles().stream().map(RoleResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/role/{roleId}")
    @Secured(ROLE_WRITE)
    public RoleResponse getRole(@PathVariable Long roleId) {
        return new RoleResponse(userService.getRole(new RoleRequest(roleId)));
    }

    @PostMapping("/role")
    @Secured(ROLE_WRITE)
    public RoleResponse addRole(@RequestBody RoleRequest request) {
        return new RoleResponse(userService.addRole(request));

    }

    @PutMapping("/role")
    @Secured(ROLE_WRITE)
    public RoleResponse updateRole(@RequestBody RoleRequest request) {

        return new RoleResponse(userService.updateRole(request));
    }


    @DeleteMapping("/role/{roleId}")
    @Secured(ROLE_WRITE)
    public void deleteRole(@PathVariable Long roleId) {
        userService.deleteRole(new RoleRequest(roleId));
    }

    // API for Token Validation
    @GetMapping("/tokenCheck")
    public void tokenValid() {
    }
}
