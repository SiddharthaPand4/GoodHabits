package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.core.SynVisionUser;
import io.synlabs.synvision.entity.CurrentUser;
import io.synlabs.synvision.entity.core.Role;
import io.synlabs.synvision.entity.core.Privilege;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.PrivilegeRepository;
import io.synlabs.synvision.jpa.SynVisionUserRepository;
import io.synlabs.synvision.jpa.RoleRepository;
import io.synlabs.synvision.views.core.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private SynVisionUserRepository userRepository;

    @Autowired
    UserMenuBuilder menuBuilder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public SynVisionUser validate(LoginRequest request) {

        SynVisionUser user;
        if (StringUtils.isEmpty(request.getEmail())) {
            user = userRepository.findByUsernameAndActiveTrue(request.getUsername());
        } else {
            user = userRepository.findByEmailAndActiveTrue(request.getEmail());
        }


        if (user != null && encoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.setLastLogin(DateTime.now().toDate());
            userRepository.saveAndFlush(user);
            return user;
        }

        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        SynVisionUser user = userRepository.findByEmailOrUsername(subject, subject);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s was not found", subject));

        }
        return new CurrentUser(user, new String[]{"ROLE_USER"});
    }

    public SynVisionUser checkLogin(String username, String password) {
        SynVisionUser user = userRepository.findByEmail(username);

        if (user == null) return null;

        if (encoder.matches(password, user.getPasswordHash())) {

            List<String> lroles = new LinkedList<>();
            for (Role role : user.getRoles()) {
                lroles.add("ROLE_" + role.getName());
            }

            String[] roles = lroles.toArray(new String[0]);
            Authentication result = new UsernamePasswordAuthenticationToken(new CurrentUser(user, roles), user.getPasswordHash(), AuthorityUtils.createAuthorityList(roles));
            SecurityContextHolder.getContext().setAuthentication(result);
            return user;
        }

        return null;
    }

    public SynVisionUser createUser(UserRequest request) {

        validateUser(request);
        SynVisionUser user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            throw new ValidationException(String.format("Already exist [email=%s]", request.getEmail()));
        }

        user = request.toEntity();
        user.setOrg(getAtccUser().getOrg());

        for (String role : request.getRoles()) {
            user.addRole(roleRepository.getOneByName(role));
        }

        //TODO do we send an email also?
        //String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        logger.info("Temp password: {}", "nhaidemo");
        user.setPasswordHash(encoder.encode("nhaidemo"));

        return userRepository.save(user);
    }

    public SynVisionUser updateUser(UserRequest request) {

        validateUser(request);
        SynVisionUser user = userRepository.findByEmail(request.getEmail());
        request.toEntity(user);
        user.setOrg(getAtccUser().getOrg());

        user.getRoles().clear();
        for (String role : request.getRoles()) {
            user.addRole(roleRepository.getOneByName(role));
        }

        return userRepository.save(user);
    }

    private void validateUser(UserRequest request) {
        if (StringUtils.isEmpty(request.getFirstName())) {
            throw new ValidationException("First Name is required.");
        }


        if (StringUtils.isEmpty(request.getEmail())) {
            throw new ValidationException("User email is required.");
        }

        if (StringUtils.isEmpty(request.getUserName())) {
            throw new ValidationException("Username is required.");
        }

        if (StringUtils.isEmpty(request.getLastName())) {
            throw new ValidationException("Lastname is required.");
        }
    }

    public Set<String> getUserPrivileges(SynVisionUser user) {
        return user.getPrivileges();
    }

    public void deleteUser(UserRequest request) {

        SynVisionUser user = userRepository.getOne(request.getId());

        if (user == null) {
            throw new NotFoundException("Cannot locate user");
        }

        if (Objects.equals(user.getId(), getAtccUser().getId())) {
            throw new ValidationException("You cannot deactivate yourself!");
        }
        user.getRoles().clear();
        user.setActive(false);
        userRepository.saveAndFlush(user);
    }

    public List<UserResponse> listUsers() {
        List<SynVisionUser> users = userRepository.findAllByOrgAndActiveTrue(getAtccUser().getOrg());
        return users.stream().map(UserResponse::new).collect(Collectors.toList());
    }

    public SynVisionUser getUserDetail(UserRequest request) {
        return userRepository.getOne(request.getId());
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Menu getCurrentUserMenu() {
        return menuBuilder.getMenu();
    }

    public void deleteRole(RoleRequest roleRequest) {
        Role role = roleRepository.getOne(roleRequest.getId());
        if (getCurrentUser().getRoles().contains(role)) {
            throw new ValidationException("You can't delete role assigned to you ");
        }
        try {
            roleRepository.delete(role);
        } catch (Exception e) {
            throw new ValidationException("This Role is assigned to some User. Disable the User first ");
        }
    }

    public Role getRole(RoleRequest roleRequest) {
        return roleRepository.getOne(roleRequest.getId());

    }

    public Role addRole(RoleRequest request) {
        Role role = roleRepository.findByName(request.getName());
        if (role != null) {
            throw new ValidationException(String.format("Already exist Role with name ", request.getName()));
        }
        role = request.toEntity();
        for (String privilege : request.getPrivileges()) {
            role.addPrivilege(privilegeRepository.getOneByName(privilege));
        }

        return roleRepository.save(role);

    }

    public Role updateRole(RoleRequest request) {
        Role role = roleRepository.findByName(request.getName());
        request.toEntity(role);
        role.getPrivileges().clear();
        for (String privilege : request.getPrivileges()) {
            role.addPrivilege(privilegeRepository.getOneByName(privilege));
        }

        return roleRepository.save(role);
    }

}
