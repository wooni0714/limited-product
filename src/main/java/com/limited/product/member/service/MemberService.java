package com.limited.product.member.service;

import com.limited.product.common.exception.BusinessException;
import com.limited.product.member.dto.SignUpRequest;
import com.limited.product.member.entity.Member;
import com.limited.product.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.limited.product.common.Constants.ALREADY_REGISTERED_MEMBER;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void signUpMember(SignUpRequest signUpRequest) {
        boolean existsByUserId = memberRepository.existsByUserId(signUpRequest.userId());

        if (existsByUserId) {
            throw new BusinessException(ALREADY_REGISTERED_MEMBER);
        }

        Member member = signUpRequest.toMemberEntity();
        memberRepository.save(member);
    }
}
