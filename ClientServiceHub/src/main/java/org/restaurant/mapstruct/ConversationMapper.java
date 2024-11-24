package org.restaurant.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

}
