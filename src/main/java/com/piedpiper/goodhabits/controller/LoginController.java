package com.piedpiper.goodhabits.controller;

import com.piedpiper.goodhabits.entity.GoodHabitsUser;
import com.piedpiper.goodhabits.ex.AuthException;
import com.piedpiper.goodhabits.service.UserService;
import com.piedpiper.goodhabits.view.Login.LoginRequest;
import com.piedpiper.goodhabits.view.Login.LoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${synvision.auth.secretkey}")
    private String secretkey;

    @Value("${synvision.auth.ttl_hours}")
    private int ttlhours;

    private Key key;

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        logger.info("Loading key from secret key");
        this.key = Keys.hmacShaKeyFor(secretkey.getBytes());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest login) {

        GoodHabitsUser user = userService.validate(login);

        if (user == null) {
            throw new AuthException("Invalid login by user " + login.getEmail() + " " + login.getUsername());
        }

        String authToken = Jwts.builder()
                .setSubject(login.getEmail() == null ? login.getUsername() : login.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(getAuthExpiration())
                .signWith(key).compact();
        return new LoginResponse(authToken);
    }

    private Date getAuthExpiration() {
        return new DateTime().plusDays(ttlhours).toDate();
    }
}
