package com.limited.product.entryLine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntryLineService {
    private final EntryLineQueueService entryLineQueueService;

    public String waitingUser(String userId) {
        long currentActive = entryLineQueueService.countConnected();

        if (currentActive < 10) {
            entryLineQueueService.addToConnected(userId);
            return "접속 성공";
        } else {
            Double myScore = entryLineQueueService.getMyScore(userId);
            if (myScore == null) {
                entryLineQueueService.addToWaitingQueue(userId);
            }
            long rank = entryLineQueueService.getWaitingCount(userId);
            long waitingCount = entryLineQueueService.getMyBehindInclusive(myScore);

            return "현재 대기 순번: " + (rank + 1) + ", 내 뒤에" + waitingCount + "명의 대기자가 있어요.";
        }
    }
}
