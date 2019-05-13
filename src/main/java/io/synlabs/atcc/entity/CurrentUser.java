package io.synlabs.atcc.entity;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {

    private AtccUser user;

    public CurrentUser(AtccUser user, String[] roles) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(roles));
        this.user = user;
    }

    public AtccUser getUser() {
        return user;
    }

    public void setUser(AtccUser user) {
        this.user = user;
    }
}
