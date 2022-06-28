package mgmsports.dao.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.JoinColumn;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Competition entity
 *
 * @author dntvo
 */
@Data
@Entity
@Table(name = "competition")
public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String competitionId = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Account host;

    @ManyToOne
    @JoinColumn(name = "invitee_id")
    private Account invitee;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdDate;
}