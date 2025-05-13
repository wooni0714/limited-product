package com.limited.product.waiting.scheduler;

import com.limited.product.waiting.service.WaitingQueueService;
import com.limited.product.waiting.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static com.limited.product.common.Constants.CONNECTED_SUCCESSFULLY;

@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingScheduler {
    private final WaitingQueueService waitingQueueService;
    private final SseNotificationService sseNotificationService;

    @Scheduled(fixedRate = 1000)
    public void waitingScheduler() {
        processEntry();
        notifyWaitingUsers();
    }

    private void processEntry() {
        long connected = waitingQueueService.countConnected();
        if (connected >= 10) return;

        Optional<String> nextUser = waitingQueueService.getWaitingQueue();
        nextUser.ifPresent(userId -> {
            try {
                waitingQueueService.removeFromWaitingQueue(userId);
                waitingQueueService.addToConnected(userId);
                sseNotificationService.send(userId, CONNECTED_SUCCESSFULLY);
                log.info("입장 처리: {}", userId);
            } catch (Exception e) {
                log.error("입장 처리 실패 - userId: {}, 에러: {}", userId, e.getMessage());
                waitingQueueService.removeFromConnected(userId);
            }
        });
    }

    private void notifyWaitingUsers() {
        Set<String> waiters = waitingQueueService.getAllWaitingUsers();

        for (String userId : waiters) {
            try {
                Double score = waitingQueueService.getMyScore(userId);
                if (score == null) continue;

                long rank = waitingQueueService.getWaitingCount(userId);
                long behind = waitingQueueService.getMyBehindInclusive(score);

                String message = formatRankMessage(rank, behind);
                sseNotificationService.send(userId, message);
            } catch (Exception e) {
                log.error("대기열 알림 전송 실패 - userId: {}, 에러: {}", userId, e.getMessage());
            }
        }
    }

    private String formatRankMessage(long rank, long behind) {
        return String.format("현재 순번: %d, 뒤에 %d명", rank + 1, behind);
    }
}