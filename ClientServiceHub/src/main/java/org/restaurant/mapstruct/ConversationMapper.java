package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.ConversationEntity;
import org.restaurant.request.ConversationRequest;
import org.restaurant.response.ConversationResponse;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

    ConversationEntity conversationRequestToConversationEntity(ConversationRequest conversationRequest);
    ConversationResponse conversationEntityToConversationResponse(ConversationEntity conversationEntity);
}
