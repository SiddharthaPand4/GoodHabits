package io.synlabs.atcc.service;

import io.synlabs.atcc.entity.AtccUser;
import io.synlabs.atcc.entity.CurrentUser;
import io.synlabs.atcc.entity.Role;
import io.synlabs.atcc.jpa.AtccUserRepository;
import io.synlabs.atcc.jpa.RoleRepository;
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

import java.util.LinkedList;
import java.util.List;

@Service
public class UserService extends BaseService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private AtccUserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AtccUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s was not found", email));

        }
        return new CurrentUser(user, new String[]{"ROLE_USER"});
    }

    public AtccUser checkLogin(String username, String password) {
        AtccUser user = userRepository.findByEmail(username);

        if (user == null) return null;

        if (encoder.matches(password, user.getPasswordHash())) {

            List<String> lroles = new LinkedList<>();
            for(Role role: user.getRoles()) {
                lroles.add("ROLE_" + role.getName());
            }

            String[] roles = lroles.toArray(new String[0]);
            Authentication result = new UsernamePasswordAuthenticationToken(new CurrentUser(user, roles), user.getPasswordHash(), AuthorityUtils.createAuthorityList(roles));
            SecurityContextHolder.getContext().setAuthentication(result);
            return user;
        }

        return null;
    }


    public AtccUser createUser(String username, String email) {
        AtccUser user = new AtccUser();
        user.setExternalId(username);
        user.setActive(true);
        user.setEmail(email);
        user.getRoles().add(roleRepository.getOneByName("USER"));

        //TODO do we send an email also?
        //String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        logger.info("Temp password: {}", "nhaidemo");
        user.setPasswordHash(encoder.encode("nhaidemo"));
        return userRepository.save(user);
    }

}
