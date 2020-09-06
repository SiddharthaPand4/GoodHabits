package com.piedpiper.goodhabits.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CommunityMembership extends BaseEntity {

    @ManyToOne
    private Community community;

    @ManyToOne
    private GoodHabitsUser member;

    public CommunityMembership(Community community, GoodHabitsUser member) {
        this.community = community;
        this.member = member;
    }

}
