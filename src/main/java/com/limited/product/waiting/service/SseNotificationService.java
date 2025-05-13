package com.limited.product.waiting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseNotificationService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final String EVENT_NAME = "waiting-number";

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(1000L * 60 * 60);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        log.info("SSE 연결 완료 - userId: {}", userId);

        emitter.onTimeout(() -> emitters.remove(userId));
        log.info("SSE 타임아웃 - userId: {}", userId);

        emitter.onError(e -> {
            emitters.remove(userId);
            log.warn("SSE 에러 발생 - userId: {}, 에러: {}", userId, e.getMessage());
        });

        return emitter;
    }

    public void send(String userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name(EVENT_NAME)
                                .data(message)
                );
            } catch (IOException e) {
                log.warn("SSE 전송 실패 - userId: {}, 에러: {}", userId, e.getMessage());
                emitters.remove(userId);
            } catch (IllegalStateException e) {
                log.warn("이미 종료된 emitter - userId: {}, 에러: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }
}
