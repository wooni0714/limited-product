package com.limited.product.member.dto;

import com.limited.product.member.entity.Member;

public record SignUpRequest(
        String userId
) {

    public Member toMemberEntity() {
        return new Member(userId);
    }
}
