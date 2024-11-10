package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.restaurant.model.MessageEntity;
import org.restaurant.request.MessageRequest;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageEntity messageRequestToMessageEntity(MessageRequest messageRequest);
}
