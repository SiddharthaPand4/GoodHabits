package com.piedpiper.goodhabits.service;

import com.piedpiper.goodhabits.entity.CurrentUser;
import com.piedpiper.goodhabits.entity.GoodHabitsUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseService {

    public GoodHabitsUser getCurrentUser() {
        return getGoodHabitsUser();
    }

    public static GoodHabitsUser getGoodHabitsUser() {
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
