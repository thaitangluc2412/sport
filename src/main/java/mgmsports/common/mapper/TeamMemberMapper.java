package mgmsports.common.mapper;

import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Profile;
import mgmsports.model.TeamMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Profile and account Data transfer
 *
 * @author vtlu
 */
@Mapper(componentModel = "spring")
public interface TeamMemberMapper {
    @Mappings({

            @Mapping(source = "profile.imageLink",target = "imageLink"),
            @Mapping(source = "profile.fullName",target = "fullName"),
            @Mapping(source = "account.accountId",target = "accountId")
    }
    )
    TeamMemberDto accountAndProfileToTeamMemberDto(Account account, Profile profile);
}
