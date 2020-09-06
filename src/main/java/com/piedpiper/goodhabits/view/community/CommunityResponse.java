package com.piedpiper.goodhabits.view.community;

import com.piedpiper.goodhabits.view.common.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityResponse implements Response {

    private Long communityId;
    private String name;
    private String description;
    private String adminName;
    private Long adminId;

    public void setCommunityId(Long communityId) {
        this.communityId = mask(communityId);
    }

    public void setAdminId(Long adminId) {
        this.adminId = mask(adminId);
    }

}
