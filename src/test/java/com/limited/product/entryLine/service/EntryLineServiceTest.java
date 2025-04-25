package com.limited.product.entryLine.service;

import com.limited.product.member.dto.SignUpRequest;
import com.limited.product.member.repository.MemberRepository;
import com.limited.product.member.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

import static com.limited.product.common.Constants.CONNECTED_SUCCESSFULLY;
import static com.limited.product.common.Constants.USER_ADDED_TO_QUEUE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntryLineServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private EntryLineService entryLineService;

    @Autowired
    private EntryLineQueueService entryLineQueueService;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        memberRepository.deleteAll();

        for (int i = 0; i < 20; i++) {
            String userId = "user" + i;
            memberService.signUpMember(new SignUpRequest(userId));
        }
    }

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 접속자_10명이하_입장_테스트() {
        for (int i = 0; i < 10; i++) {
            String userId = "user" + i;
            String result = entryLineService.waitingUser(userId);
            assertEquals(CONNECTED_SUCCESSFULLY, result);
        }
        assertEquals(10, entryLineQueueService.countConnected());
    }

    @Test
    void 접속자_10명이상_대기열_등록_테스트() {
        for (int i = 0; i < 10; i++) {
            entryLineService.waitingUser("user" + i);
        }

        for (int i = 10; i < 20; i++) {
            String userId = "user" + i;
            String result = entryLineService.waitingUser(userId);
            assertEquals(USER_ADDED_TO_QUEUE, result);
            assertNotNull(entryLineQueueService.getWaitingCount(userId));
        }
        assertEquals(10, entryLineQueueService.countWaiting());
    }
}