package com.piedpiper.goodhabits.entity;

import com.piedpiper.goodhabits.view.community.CommunityRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Community extends BaseEntity {

    private String name;

    private String description;

    @ManyToOne
    private GoodHabitsUser admin;

    public Community(CommunityRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
    }

}
