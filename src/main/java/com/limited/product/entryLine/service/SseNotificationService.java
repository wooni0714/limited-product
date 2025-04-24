package com.limited.product.entryLine.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseNotificationService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final String EVENT_NAME = "waiting-number";

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(1000L * 60 * 60);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));

        emitter.onTimeout(() -> emitters.remove(userId));

        emitter.onError(e -> emitters.remove(userId));

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
                emitters.remove(userId);
            }
        }
    }
}
