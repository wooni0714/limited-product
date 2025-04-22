package com.limited.product.member.controller;

import com.limited.product.member.dto.SignUpRequest;
import com.limited.product.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpMember(@RequestBody SignUpRequest signUpRequest) {
        memberService.signUpMember(signUpRequest);
        return ResponseEntity.ok("가입완료");
    }
}