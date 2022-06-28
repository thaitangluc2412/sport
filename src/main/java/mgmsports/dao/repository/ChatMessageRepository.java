package mgmsports.dao.repository;

import mgmsports.dao.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Chat message repository
 *
 * @author qngo
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    /**
     * Get most recent chat message of team
     *
     * @param teamId team id
     * @return chat message
     */
    ChatMessage findFirstByTeam_TeamIdOrderByCreatedDateDesc(String teamId);

    /**
     * Get closest chat message of team before @date
     *
     * @param teamId team id
     * @param date date
     * @return chat message
     */
    ChatMessage findFirstByTeam_TeamIdAndCreatedDateBeforeOrderByCreatedDateDesc(String teamId, Date date);

    /**
     * Get list of chat messages by team id and created date
     *
     * @param teamId team id
     * @param date date with format "yyyy-MM-dd"
     * @return list of chat messages
     */
    @Query(nativeQuery = true, value = "select * from chat_message where team_id = :id and cast(created_date as date) = :date order by created_date asc")
    List<ChatMessage> findMessagesByTeamIdAndCreatedDate(@Param("id") String teamId, @Param("date") Date date);
}
