package com.piedpiper.goodhabits.view.community;

import com.piedpiper.goodhabits.view.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCommunityRequest implements Request {

    private String name;
    private String description;

}
