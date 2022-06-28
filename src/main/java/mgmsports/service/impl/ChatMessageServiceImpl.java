package mgmsports.service.impl;

import mgmsports.common.mapper.ChatMessageMapper;
import mgmsports.dao.entity.ChatMessage;
import mgmsports.dao.entity.Profile;
import mgmsports.dao.repository.ChatMessageRepository;
import mgmsports.dao.repository.ProfileRepository;
import mgmsports.model.ChatMessageDto;
import mgmsports.service.ChatMessageService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implement interface Chat message service
 *
 * @author qngo
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private ChatMessageRepository chatMessageRepository;
    private ChatMessageMapper chatMessageMapper;
    private ProfileRepository profileRepository;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper, ProfileRepository profileRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
        this.profileRepository = profileRepository;
    }

    /**
     * Create a chat message and set full name, image link for chat message dto
     *
     * @param chatMessageDto chat message dto
     */
    @Override
    public ChatMessageDto createChatMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = chatMessageMapper.chatMessageDtoToChatMessage(chatMessageDto);
        chatMessageRepository.save(chatMessage);

        Profile p = profileRepository.findProfileByAccount_AccountId(chatMessageDto.getSenderId());
        chatMessageDto.setImageLink(p.getImageLink());
        chatMessageDto.setFullName(p.getFullName());
        return chatMessageDto;
    }

    /**
     * Get list of most recent chat messages dto of team
     *
     * @param teamId team id
     * @return list of chat messages dto
     */
    @Override
    public List<ChatMessageDto> loadChat(String teamId) {
        ChatMessage mostRecentMessage = getMostRecentMessage(teamId);
        if (mostRecentMessage == null) {
            return null;
        } else {
            return getChatMessageDtos(teamId, mostRecentMessage.getCreatedDate());
        }
    }

    /**
     * Get list of closest chat messages dto before the created date of @chatMessageDto
     *
     * @param chatMessageDto chat message dto
     * @return list of chat messages dto
     */
    @Override
    public List<ChatMessageDto> loadHistory(ChatMessageDto chatMessageDto) {
        ChatMessage prevChatMessageDto = getClosestMessage(chatMessageDto.getTeamId(), chatMessageDto.getCreatedDate());
        if (prevChatMessageDto == null) {
            return null;
        } else {
            return getChatMessageDtos(chatMessageDto.getTeamId(), prevChatMessageDto.getCreatedDate());
        }
    }

    /**
     * Return date with format "yyyy-MM-dd"
     *
     * @param date date
     * @return date with format "yyyy-MM-dd"
     */
    private Date formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(date);
        Date newFormattedDate = new Date();
        try {
            newFormattedDate = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newFormattedDate;
    }

    /**
     * Get most recent chat message of team
     *
     * @param teamId team id
     * @return chat message dto
     */
    private ChatMessage getMostRecentMessage(String teamId) {
        return chatMessageRepository.findFirstByTeam_TeamIdOrderByCreatedDateDesc(teamId);
    }

    /**
     * Get closest chat message of team before @date
     *
     * @param teamId team id
     * @param date   date
     * @return chat message dto
     */
    private ChatMessage getClosestMessage(String teamId, Date date) {
        return chatMessageRepository.findFirstByTeam_TeamIdAndCreatedDateBeforeOrderByCreatedDateDesc(teamId, date);
    }

    /**
     * Get list of chat messages dto by team id and created date
     *
     * @param teamId      team id
     * @param createdDate created date of chat message
     * @return list of chat messages dto
     */
    private List<ChatMessageDto> getChatMessageDtos(String teamId, Date createdDate) {
        List<ChatMessageDto> chatMessageDtos = chatMessageRepository.findMessagesByTeamIdAndCreatedDate(teamId, formatDate(createdDate))
                .stream()
                .map(chatMessageMapper::chatMessageToChatMessageDto)
                .collect(Collectors.toList());
        for (ChatMessageDto chatMessageDto : chatMessageDtos) {
            Profile profile = profileRepository.findProfileByAccount_AccountId(chatMessageDto.getSenderId());
            if(profile != null) {
                chatMessageDto.setFullName(profile.getFullName());
                chatMessageDto.setImageLink(profile.getImageLink());
            } else {
                chatMessageDto.setFullName("");
                chatMessageDto.setImageLink("");
            }
        }
        return chatMessageDtos;
    }
}
