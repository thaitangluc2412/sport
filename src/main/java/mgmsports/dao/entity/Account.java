package mgmsports.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mgmsports.model.AccountDto;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Account entity
 *
 * @author hbchuc
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "account")
public class Account extends AbstractUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String accountId = UUID.randomUUID().toString();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "account")
    private Profile profile;

    @Column(name = "fb_user_id", unique = true)
    private String fbUserId;
    @Column(name = "social_id", unique = true)
    private String socialId;

    @Column(name = "email")
    private String email;

    @Column(name = "user_name", unique = true)
    private String userName;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @OneToMany(mappedBy = "account")
    private List<Activity> activities;

    @ManyToMany(mappedBy = "accounts")
    private List<Team> teams;

    @OneToMany(mappedBy = "host")
    private List<Competition> listHosts;

    @OneToMany(mappedBy = "invitee")
    private List<Competition> listInvitees;

    @OneToMany(mappedBy = "account")
    private List<LocationSuggestion> suggestions;
    /**
     * Makes a Account DTO
     */
    public AccountDto toAccountDto() {
        AccountDto accountDto = new AccountDto();

        accountDto.setId(accountId);
        accountDto.setEmail(email);
        accountDto.setUsername(userName);
        accountDto.setPassword(password);
        accountDto.setRoles(getRoles());
        accountDto.setGoodUser(true);

        return accountDto;

    }

    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> chatMessages;
}