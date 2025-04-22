package com.limited.product.entryLine.scheduler;

import com.limited.product.entryLine.service.EntryLineQueueService;
import com.limited.product.entryLine.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntryLineScheduler {
    private final EntryLineQueueService entryLineQueueService;
    private final SseNotificationService sseNotificationService;

    @Scheduled(fixedRate = 1000)
    public void waitingScheduler() {
        log.info("Scheduler Start");

        // 최대 10명 입장 처리
        long connected = entryLineQueueService.countConnected();
        if (connected < 10) {
            Set<String> next = entryLineQueueService.getWaitingQueue(1);
            if (!next.isEmpty()) {
                String userId = next.iterator().next();
                entryLineQueueService.removeFromWaitingQueue(userId);
                entryLineQueueService.addToConnected(userId);
                sseNotificationService.send(userId, "입장하였습니다.");
                log.info("  → 입장: {}", userId);
            }
        }

        // 대기 중 유저에게 현재 순번 전송
        Set<String> waiters = entryLineQueueService.getAllWaitingUsers();
        if (waiters != null) {
            for (String userId : waiters) {
                Double score = entryLineQueueService.getMyScore(userId);
                if (score == null) continue;

                long rank = entryLineQueueService.getWaitingCount(userId);
                long behind = entryLineQueueService.getMyBehindInclusive(score);
                String msg = String.format("현재 순번: %d, 뒤에 %d명", rank + 1, behind);
                sseNotificationService.send(userId, msg);
            }
        }
    }
}