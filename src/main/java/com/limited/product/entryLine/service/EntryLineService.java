package com.limited.product.entryLine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntryLineService {
    private final EntryLineQueueService waitingQueueService;

    public String waitingUser(String userId) {
        long currentActive = waitingQueueService.countConnected();
        waitingQueueService.addToWaitingQueueIfNotConnectedOrWaiting(userId);

        if (currentActive < 10) {
            waitingQueueService.addToConnected(userId);

            return "접속 성공";
        } else {
            Double myScore = waitingQueueService.getMyScore(userId);
            if (myScore == null) {
                waitingQueueService.addToWaitingQueue(userId);
            }

            return "대기열 등록";
        }
    }
}
