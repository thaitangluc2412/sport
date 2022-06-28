package mgmsports.dao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;

/**
 * Chat message entity
 *
 * @author qngo
 */
@Data
@Entity
public class ChatMessage {

    @Id
    private String chatMessageId = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "sender_id", updatable = false)
    private Account sender;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdDate;

    private String content;

    @ManyToOne
    @JoinColumn(name = "team_id", updatable = false)
    private Team team;
}
