package mgmsports.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class TeamAccountDto {

    @NotEmpty(message = "Please provide teamId")
    @Size(max = 255, message = "teamId must be less than or equal 255")
    private String teamId;

    @NotEmpty(message = "Please provide accountId")
    @Size(max = 255, message = "accountId must be less than or equal 255")
    private String accountId;

}
