package mgmsports.controller;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.model.ChatMessageDto;
import mgmsports.service.ChatMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller that handles WebSocket chat message
 *
 * @author qngo
 */
@RestController
@RequestMapping("/api")
public class ChatMessageController {

    private ChatMessageService chatMessageService;

    /* The SimpMessagingTemplate is used to send Stomp over WebSocket messages */
    private SimpMessagingTemplate simpMessagingTemplate;

    public ChatMessageController(ChatMessageService chatMessageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Create a chat message
     *
     * @param chatMessageDto chat message dto
     * @param teamId team id
     */
    @MessageMapping("/chat.sendMessage.{teamId}")
    public void sendGroupMessage(@Payload ChatMessageDto chatMessageDto, @DestinationVariable("teamId") String teamId) {
        ChatMessageDto newChatMessageDto = chatMessageService.createChatMessage(chatMessageDto);
        simpMessagingTemplate.convertAndSend("/chat/" + teamId, newChatMessageDto);
    }

    /**
     * Get list of most recent chat messages
     *
     * @param teamId team id
     * @return list of chat message dto
     */
    @GetMapping("/chat/loadChat")
    public ResponseEntity<Object> loadChat(@RequestParam("teamId") String teamId) throws EntityNotFoundException {
        Optional<List<ChatMessageDto>> optionalChatMessageDtos = Optional.ofNullable(chatMessageService.loadChat(teamId));
        return optionalChatMessageDtos.<ResponseEntity<Object>>map(chatMessageDtos -> new ResponseEntity<>(chatMessageDtos, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("[]", HttpStatus.OK));
    }

    /**
     * Get list of closest chat messages before the created date of @chatMessageDto
     *
     * @return list of chat message dto
     */
    @PostMapping("/chat/history")
    public ResponseEntity<Object> loadHistory(@Valid @RequestBody ChatMessageDto chatMessageDto) throws EntityNotFoundException {
        Optional<List<ChatMessageDto>> optionalChatMessageDtos = Optional.ofNullable(chatMessageService.loadHistory(chatMessageDto));
        return optionalChatMessageDtos.<ResponseEntity<Object>>map(chatMessageDtos -> new ResponseEntity<>(chatMessageDtos, HttpStatus.OK)).
                orElseGet(() -> new ResponseEntity<>("[]", HttpStatus.OK));
    }
}
