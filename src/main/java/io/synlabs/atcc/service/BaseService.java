package io.synlabs.atcc.service;

import io.synlabs.atcc.entity.AtccUser;
import io.synlabs.atcc.entity.CurrentUser;
import io.synlabs.atcc.views.SortRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

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

    public static boolean isDescending(List<SortRequest> sortedRequests){
        if(CollectionUtils.isEmpty(sortedRequests)){
            return true;
        }
        if (sortedRequests.get(0) == null || sortedRequests.get(0).getDesc() == null) {
            return true;
        }
        return sortedRequests.get(0).getDesc();
    }

    public static String getDefaultSortId(List<SortRequest> sortedRequests, String defaultSortId){
        if(CollectionUtils.isEmpty(sortedRequests)){
            return defaultSortId;
        }
        if (sortedRequests.get(0) == null || StringUtils.isEmpty(sortedRequests.get(0).getId())) {
            return defaultSortId;
        }
        return sortedRequests.get(0).getId();
    }
}
