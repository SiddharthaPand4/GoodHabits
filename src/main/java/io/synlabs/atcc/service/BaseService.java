package io.synlabs.atcc.service;

import io.synlabs.atcc.entity.AtccUser;
import io.synlabs.atcc.entity.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseService {
    public AtccUser getCurrentUser() {
        return getAtccUser();
    }

    public static AtccUser getAtccUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if ((authentication.getPrincipal() instanceof CurrentUser)) {
            return ((CurrentUser) authentication.getPrincipal()).getUser();
        }
        return null;
    }
}
