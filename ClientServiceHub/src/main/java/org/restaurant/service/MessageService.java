package org.restaurant.service;

import org.restaurant.request.MessageRequest;
import org.springframework.http.ResponseEntity;


public interface MessageService {
    ResponseEntity<?> postMessage(MessageRequest messageRequest);
}
