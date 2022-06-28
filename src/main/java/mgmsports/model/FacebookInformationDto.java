package mgmsports.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class FacebookInformationDto {

    @NotEmpty(message = "Please provide fbUserId")
    @Size(max = 255, message = "fbUserId must be less than or equal 255")
    private String fbUserId;

    @Email
    private String email;

    @NotEmpty(message = "Please provide fbUserName")
    @Size(max = 255, message = "fbUserName must be less than or equal 255")
    private String fbUserName;

    @NotEmpty(message = "Please provide imageLink")
    @Size(max = 255, message = "imageLink must be less than or equal 255")
    private String imageLink;

}
