package mgmsports.model;

import lombok.Data;

import java.util.Date;

/**
 * Chat message dto
 *
 * @author qngo
 */
@Data
public class ChatMessageDto {

    private String content;

    private String senderId;

    private String teamId;

    private Date createdDate;

    private String fullName;

    private String imageLink;
}
