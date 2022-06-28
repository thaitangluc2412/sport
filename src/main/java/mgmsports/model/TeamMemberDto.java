package mgmsports.model;

import lombok.Data;

/**
 * Account Data transfer
 *
 * @author vtlu
 */
@Data
public class TeamMemberDto {
    private String accountId;
    private String fullName;
    private String imageLink;
}
