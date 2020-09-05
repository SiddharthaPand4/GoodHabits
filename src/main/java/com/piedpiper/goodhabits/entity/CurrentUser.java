package com.piedpiper.goodhabits.entity;


import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {
    private GoodHabitsUser user;

    public CurrentUser(GoodHabitsUser user, String[] roles) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(roles));
        this.user = user;
    }

    public GoodHabitsUser getUser() {
        return user;
    }

    public void setUser(GoodHabitsUser user) {
        this.user = user;
    }

}
