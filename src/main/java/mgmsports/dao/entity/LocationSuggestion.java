package mgmsports.dao.entity;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;

/**
 * Suggestion Location Entity
 *
 * @author qngo
 */
@Entity
@Data
@Table(name = "location_suggestion")
public class LocationSuggestion {
    private static final long serialVersionUID = 1L;

    @Id
    private String suggestionId = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "account_id", updatable = false)
    private Account account;

    @Column(name = "suggestion")
    private String suggestion;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "last_enter_date")
    private Date lastEnterDate;
}
