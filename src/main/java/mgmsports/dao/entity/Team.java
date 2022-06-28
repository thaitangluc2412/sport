package mgmsports.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Team entity
 *
 * @author dntvo
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String teamId = UUID.randomUUID().toString();

    @Column(name = "name")
    private String name;

    @Column
    private String hostId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "team_account",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id"))
    private List<Account> accounts = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(updatable = false)
    private Date createdDate;

    @Column()
    private boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column
    private Date lastModifiedDate;

    @OneToMany(mappedBy = "team")
    private List<ChatMessage> chatMessages;

}