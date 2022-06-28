package mgmsports.dao.entity;

import lombok.Data;
import mgmsports.model.ActivityType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Activity entity
 *
 * @author qngo
 */
@Entity
@Data
@Table(name = "activity")
public class Activity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String activityId = UUID.randomUUID().toString();

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @ManyToOne
    @JoinColumn(name = "account_id", updatable = false)
    private Account account;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "workout_type")
    private String workoutType;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "activity_date")
    private Date activityDate;

    @Column(name = "image_link")
    private String imageLink;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "activity_type", updatable = false)
    @Enumerated(EnumType.ORDINAL)
    private ActivityType activityType;
}
