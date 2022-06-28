package mgmsports.dao.entity;

import lombok.Data;
import mgmsports.model.Status;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Profile entity
 *
 * @author Chuc Ba Hieu
 */
@Entity
@Data
@Table(name = "profile")
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String profileId = UUID.randomUUID().toString();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "image")
    private String imageLink;

    @Column(name = "backup_image")
    private String backupImage;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "hobbies")
    private String hobbies;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}