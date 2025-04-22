package com.limited.product.entryLine.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseNotificationService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(1000L * 60 * 60);

        // userId에 emitter 저장
        emitters.put(userId, emitter);

        // 연결 종료시 emitter 제거
        emitter.onCompletion(() -> emitters.remove(userId));

        // 타임아웃 발생시 emitter 제거
        emitter.onTimeout(() -> emitters.remove(userId));

        // 오류 발생시 emitter 제거
        emitter.onError(e -> emitters.remove(userId));

        return emitter;
    }

    public void send(String userId, String message) {
        // 해당 유저 emitter 가져오기
        SseEmitter emitter = emitters.get(userId);

        // emitter가 존재하면 메세지 전송
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("waiting-number")
                                .data(message)
                );
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}
