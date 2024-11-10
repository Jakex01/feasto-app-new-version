package org.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.restaurant.request.MessageRequest;
import org.restaurant.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> postMessage(@RequestBody MessageRequest messageRequest){
        return  messageService.postMessage(messageRequest);
    }
}
