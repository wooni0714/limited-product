package com.limited.product.entryLine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class EntryLineQueueService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String WAITING_KEY = "waitingQueue";
    private static final String CONNECTED_KEY = "connectedUsers";


    public long countConnected() {
        return Objects.requireNonNullElse(redisTemplate.opsForZSet().size(CONNECTED_KEY), 0L);
    }

    public void addToConnected(String userId) {
        redisTemplate.opsForZSet().add(CONNECTED_KEY, userId, System.currentTimeMillis());
    }


    public void addToWaitingQueue(String userId) {
        redisTemplate.opsForZSet().add(WAITING_KEY, userId, System.currentTimeMillis());
        log.info("대기열에 등록된 사용자: {}", userId);
    }

    public Long getWaitingCount(String userId) {
        Long rank = redisTemplate.opsForZSet().rank(WAITING_KEY, userId);
        return rank == null ? -1 : rank;
    }

    public Long getMyBehindInclusive(Double count) {
        return redisTemplate.opsForZSet().count(WAITING_KEY, count + 1, Double.MAX_VALUE);
    }

    public Double getMyScore(String userId) {
        return redisTemplate.opsForZSet().score(WAITING_KEY, userId);
    }

    public Set<String> getWaitingQueue(int count) {
        return redisTemplate.opsForZSet().range(WAITING_KEY, 0, count - 1);
    }

    public void removeFromWaitingQueue(String userId) {
        redisTemplate.opsForZSet().remove(WAITING_KEY, userId);
    }

    public Set<String> getAllWaitingUsers() {
        return redisTemplate.opsForZSet().range(WAITING_KEY, 0, -1);
    }

    public void addToWaitingQueueIfNotConnectedOrWaiting(String userId) {
        boolean isConnected = redisTemplate.opsForZSet().score(CONNECTED_KEY, userId) != null;
        boolean isWaiting = redisTemplate.opsForZSet().score(WAITING_KEY, userId) != null;

        if (!isConnected && !isWaiting) {
            redisTemplate.opsForZSet().add(WAITING_KEY, userId, System.currentTimeMillis());
            log.info("대기열에 등록된 사용자: {}", userId);
        } else {
            log.info("이미 등록된 사용자: {}", userId + ", 상태: " +
                    (isConnected ? "접속 중" : "대기 중"));
        }
    }
}
