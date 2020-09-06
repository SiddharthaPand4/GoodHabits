package com.piedpiper.goodhabits.service;

import com.piedpiper.goodhabits.entity.CurrentUser;
import com.piedpiper.goodhabits.entity.GoodHabitsUser;
import com.piedpiper.goodhabits.jpa.GoodHabitsUserRepository;
import com.piedpiper.goodhabits.jpa.PrivilegeRepository;
import com.piedpiper.goodhabits.jpa.RoleRepository;
import com.piedpiper.goodhabits.view.Login.LoginRequest;
import com.piedpiper.goodhabits.view.signup.CreateUserRequest;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService extends BaseService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private GoodHabitsUserRepository userRepository;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public GoodHabitsUser validate(LoginRequest request) {

        GoodHabitsUser user;
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
        GoodHabitsUser user = userRepository.findByEmailOrUsername(subject, subject);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s was not found", subject));

        }
        return new CurrentUser(user, new String[]{"ROLE_USER"});
    }

    public void createUser(CreateUserRequest request) {
        userRepository.saveAndFlush(new GoodHabitsUser(request));
    }

}
