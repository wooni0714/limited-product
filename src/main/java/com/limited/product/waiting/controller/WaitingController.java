package com.limited.product.waiting.controller;

import com.limited.product.waiting.dto.WaitingRequest;
import com.limited.product.waiting.service.WaitingService;
import com.limited.product.waiting.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class WaitingController {
    private final WaitingService entryLineQueueService;
    private final SseNotificationService sseNotificationService;

    @PostMapping("/connect")
    public String connect(@RequestBody WaitingRequest request) {
        return entryLineQueueService.waitingUser(request.userId());
    }

    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable String userId) {
        return sseNotificationService.subscribe(userId);
    }
}
