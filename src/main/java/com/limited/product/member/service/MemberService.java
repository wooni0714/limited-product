package com.limited.product.member.service;

import com.limited.product.common.exception.BusinessException;
import com.limited.product.member.dto.SignUpRequest;
import com.limited.product.member.entity.Member;
import com.limited.product.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void signUpMember(SignUpRequest signUpRequest) {
        boolean existsByUserId = memberRepository.existsByUserId(signUpRequest.userId());

        if (existsByUserId) {
            throw new BusinessException("이미 존재하는 회원입니다.");
        }

        Member member = signUpRequest.toMemberEntity();
        memberRepository.save(member);
    }
}
