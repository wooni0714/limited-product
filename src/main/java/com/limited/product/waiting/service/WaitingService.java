package com.limited.product.waiting.service;

import com.limited.product.common.exception.BusinessException;
import com.limited.product.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.limited.product.common.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingService {
    private final WaitingQueueService waitingQueueService;
    private final MemberRepository memberRepository;

    public String waitingUser(String userId) {
        boolean existsByUserId = memberRepository.existsByUserId(userId);
        if (!existsByUserId) {
            throw new BusinessException(MEMBER_NOT_FOUND_BY_ID);
        }

        boolean isWaiting = waitingQueueService.isWaiting(userId);

        long currentActive = waitingQueueService.countConnected();
        log.info("현재 접속자 수 : {}", currentActive);

        if (currentActive < 10) {
            waitingQueueService.addToConnected(userId);
            return CONNECTED_SUCCESSFULLY;
        }

        if (!isWaiting) {
            waitingQueueService.addToWaitingQueue(userId);
            log.info("대기열에 등록 : {}", userId);
        } else {
            return ALREADY_IN_QUEUE;
        }
        return USER_ADDED_TO_QUEUE;
    }
}