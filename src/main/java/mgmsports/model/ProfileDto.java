package mgmsports.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class ProfileDto {

    @NotEmpty(message = "Please provide profile id")
    @Size(max = 255, message = "Profile id must less than or equal 255")
    private String profileId;

    @NotEmpty(message = "Please provide account id")
    @Size(max = 255, message = "Account id must less than or equal 255")
    private String accountId;

    @NotEmpty(message = "Please provide full name")
    @Size(max = 255, message = "Full name must less than or equal 255")
    private String fullName;


    private String imageLink;

    @Size(max = 255, message = "Introduction must less than or equal 255")
    private String introduction;

    @Size(max = 255, message = "Hobbies must less than or equal 255")
    private String hobbies;

    private Status status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDate;
}