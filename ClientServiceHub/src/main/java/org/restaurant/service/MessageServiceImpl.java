package org.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.restaurant.mapstruct.MessageMapper;
import org.restaurant.model.MessageEntity;
import org.restaurant.repository.ConversationRepository;
import org.restaurant.repository.MessageRepository;
import org.restaurant.request.MessageRequest;
import org.restaurant.validator.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ObjectsValidator<MessageRequest> messageRequestObjectsValidator;

    @Override
    public ResponseEntity<?> postMessage(MessageRequest messageRequest) {
        messageRequestObjectsValidator.validate(messageRequest);
        MessageEntity messageEntity = MessageMapper.INSTANCE.messageRequestToMessageEntity(messageRequest);
        messageRepository.save(messageEntity);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
