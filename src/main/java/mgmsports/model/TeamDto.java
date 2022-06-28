package mgmsports.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class TeamDto {

    private String teamId;

    @NotEmpty(message = "Please provide name")
    @Size(max = 255, message = "name must be less than or equal 255")
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;

    @NotEmpty(message = "Please provide hostId")
    @Size(max = 255, message = "hostId must be less than or equal 255")
    private String hostId;

    @JsonProperty
    private boolean active;

}