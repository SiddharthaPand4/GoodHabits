package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.SynVisionUser;
import io.synlabs.synvision.entity.CurrentUser;
import io.synlabs.synvision.entity.Role;
import io.synlabs.synvision.jpa.SynVisionUserRepository;
import io.synlabs.synvision.jpa.RoleRepository;
import io.synlabs.synvision.views.LoginRequest;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class UserService extends BaseService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private SynVisionUserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public SynVisionUser validate(LoginRequest request)
    {

        SynVisionUser user;
        if (StringUtils.isEmpty(request.getEmail())) {
            user = userRepository.findByUsernameAndActiveTrue(request.getUsername());
        }
        else {
            user = userRepository.findByEmailAndActiveTrue(request.getEmail());
        }


        if (user != null && encoder.matches(request.getPassword(), user.getPasswordHash()))
        {
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


    public SynVisionUser createUser(String username, String email) {
        SynVisionUser user = new SynVisionUser();
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

    public Set<String> getUserPrivileges(SynVisionUser user)
    {
        return user.getPrivileges();
    }
}
