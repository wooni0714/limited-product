package com.limited.product.limitedProduct.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitedQueueService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String CONNECTED_KEY = "connectedUsers";

    public boolean isAlreadyConnected(String userId) {
        Double score = redisTemplate.opsForZSet().score(CONNECTED_KEY, userId);
        return score != null;
    }

    public void removeFromConnected(String userId) {
        redisTemplate.opsForZSet().remove(CONNECTED_KEY, userId);
    }
}
