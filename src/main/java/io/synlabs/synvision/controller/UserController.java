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
    public Menu getMenu()
    {

        return userService.getCurrentUserMenu();
    }

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


    // Role Part Below. (All above APIs for USER)


    @GetMapping("/get/roles")
    public List<RoleResponse> roles()
    {return userService.getRoles().stream().map(RoleResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/role/{roleId}")
    public RoleResponse getRole(@PathVariable Long roleId){
        return new RoleResponse(userService.getRole(new RoleRequest(roleId)));
    }

   @PostMapping("/role")
   public RoleResponse addRole(@RequestBody RoleRequest request)
   {
       return new RoleResponse(userService.addRole(request));

   }
    @PutMapping("/role")
    public RoleResponse updateRole(@RequestBody RoleRequest request)
    {

        return new RoleResponse(userService.updateRole(request));
    }


    @DeleteMapping("/role/{roleId}")
    public void deleteRole(@PathVariable Long roleId)
    {
        userService.deleteRole(new RoleRequest(roleId));
    }

    // API for Token Validation
    @GetMapping("/tokenCheck")
    public void tokenValid(){}
}
