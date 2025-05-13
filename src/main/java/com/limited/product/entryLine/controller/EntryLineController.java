package com.limited.product.entryLine.controller;

import com.limited.product.entryLine.dto.EntryLineRequest;
import com.limited.product.entryLine.service.EntryLineService;
import com.limited.product.entryLine.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class EntryLineController {
    private final EntryLineService entryLineQueueService;
    private final SseNotificationService sseNotificationService;

    @PostMapping("/connect")
    public String connect(@RequestBody EntryLineRequest request) {
        return entryLineQueueService.waitingUser(request.userId());
    }

    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable String userId) {
        return sseNotificationService.subscribe(userId);
    }
}
