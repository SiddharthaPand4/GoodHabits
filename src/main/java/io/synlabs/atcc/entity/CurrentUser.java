package io.synlabs.atcc.entity;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {

    private SynVisionUser user;

    public CurrentUser(SynVisionUser user, String[] roles) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(roles));
        this.user = user;
    }

    public SynVisionUser getUser() {
        return user;
    }

    public void setUser(SynVisionUser user) {
        this.user = user;
    }
}
