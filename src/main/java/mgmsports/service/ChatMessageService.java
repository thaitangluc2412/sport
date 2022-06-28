package mgmsports.service;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.model.ChatMessageDto;

import java.util.List;

/**
 * Chat message service
 *
 * @author qngo
 */
public interface ChatMessageService {

    ChatMessageDto createChatMessage(ChatMessageDto chatMessageDto);

    List<ChatMessageDto> loadHistory(ChatMessageDto chatMessageDto) throws EntityNotFoundException;

    List<ChatMessageDto> loadChat(String teamId) throws EntityNotFoundException;
}
