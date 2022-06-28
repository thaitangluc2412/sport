package mgmsports.common.mapper;

import mgmsports.dao.entity.ChatMessage;
import mgmsports.model.ChatMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Map chat message entity to dto and backwards
 *
 * @author qngo
 */
@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    @Mappings({
            @Mapping(target = "chatMessageId", ignore = true),
            @Mapping(source = "senderId", target = "sender.accountId"),
            @Mapping(source = "teamId", target = "team.teamId")
    })
    ChatMessage chatMessageDtoToChatMessage(ChatMessageDto chatMessageDto);

    @Mappings({
            @Mapping(source = "sender.accountId", target = "senderId"),
            @Mapping(source = "team.teamId", target = "teamId")
    })
    ChatMessageDto chatMessageToChatMessageDto(ChatMessage chatMessage);
}
