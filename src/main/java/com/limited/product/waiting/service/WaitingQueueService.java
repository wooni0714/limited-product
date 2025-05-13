package com.limited.product.waiting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String WAITING_KEY = "waitingQueue";
    private static final String CONNECTED_KEY = "connectedUsers";


    public long countConnected() {
        return Objects.requireNonNullElse(redisTemplate.opsForZSet().size(CONNECTED_KEY), 0L);
    }

    public void addToConnected(String userId) {
        redisTemplate.opsForZSet().add(CONNECTED_KEY, userId, System.currentTimeMillis());
    }

    public void removeFromConnected(String userId) {
        redisTemplate.opsForZSet().remove(CONNECTED_KEY, userId);
        log.info("연결된 사용자 제거: {}", userId);
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

    public Optional<String> getWaitingQueue() {
        Set<String> users = redisTemplate.opsForZSet().range(WAITING_KEY, 0, 0);
        assert users != null;
        return users.stream().findFirst();
    }

    public void removeFromWaitingQueue(String userId) {
        redisTemplate.opsForZSet().remove(WAITING_KEY, userId);
    }

    public Set<String> getAllWaitingUsers() {
        return redisTemplate.opsForZSet().range(WAITING_KEY, 0, -1);
    }

    public boolean isWaiting(String userId) {
        return redisTemplate.opsForZSet().score(WAITING_KEY, userId) != null;
    }

    public long countWaiting() {
        Long result = redisTemplate.opsForZSet().zCard(WAITING_KEY);
        return result != null ? result : 0L;
    }
}
