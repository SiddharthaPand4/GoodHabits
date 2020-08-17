package io.synlabs.synvision.views.frs;

import io.synlabs.synvision.views.common.PageResponse;

import java.util.List;

public class FrsUserPageResponse extends PageResponse<FrsUserResponse> {

    private List<FrsUserResponse> users;

    public FrsUserPageResponse(int pageSize, int pageCount, int pageNumber, List<FrsUserResponse> users)
    {
        super(pageSize, pageCount, pageNumber);
        this.users = users;
    }

    public List<FrsUserResponse> getUsers() {
        return users;
    }
}
