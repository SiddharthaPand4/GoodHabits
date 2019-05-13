package io.synlabs.atcc.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    public static void main(String []args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("password> " + encoder.encode("hello"));
    }
}
