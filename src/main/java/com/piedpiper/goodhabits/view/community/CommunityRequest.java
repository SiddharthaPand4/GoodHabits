package com.piedpiper.goodhabits.view.community;

import com.piedpiper.goodhabits.view.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityRequest implements Request {

    private Long communityId;
    private String name;
    private String description;
    private Long adminId;

    public Long getCommunityId() {
        return unmask(this.communityId);
    }

    public Long getAdminId() {
        return unmask(this.adminId);
    }

}
