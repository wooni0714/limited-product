package com.limited.product.entryLine.controller;

import com.limited.product.entryLine.dto.EntryLineRequest;
import com.limited.product.entryLine.service.EntryLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EntryLineController {
    private final EntryLineService entryLineQueueService;

    @PostMapping("/connect")
    public String connect(@RequestBody EntryLineRequest request) {
        return entryLineQueueService.waitingUser(request.userId());
    }
}
