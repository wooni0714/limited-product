package com.limited.product.entryLine.scheduler;

import com.limited.product.entryLine.service.EntryLineQueueService;
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

    @Scheduled(fixedRate = 2000)
    public void waitingScheduler() {
        log.info("스케쥴링 시작");
        long connectedCount = entryLineQueueService.countConnected();
        log.info("현재 접속된 사용자 수: {}", connectedCount);

        if (connectedCount >= 10) {
            return;
        }

        Set<String> topWaiters = entryLineQueueService.getWaitingQueue(1);

        if (topWaiters != null && !topWaiters.isEmpty()) {
            String userId = topWaiters.iterator().next();
            entryLineQueueService.addToConnected(userId);
            entryLineQueueService.removeFromWaitingQueue(userId);
            log.info("입장 userId : {}", userId);
        } else {
            log.info("대기열에 대기자가 없습니다.");
        }
    }
}