package mgmsports.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * Competition Dto
 *
 * @author dntvo
 */
@Data
public class CompetitionDto {

    private String competitionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @NotEmpty
    private String hostId;

    private UserInforDto host;

    @NotEmpty
    private String inviteeId;

    private UserInforDto invitee;
}