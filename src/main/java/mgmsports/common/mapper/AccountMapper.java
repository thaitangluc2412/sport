package mgmsports.common.mapper;
import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Profile;
import mgmsports.model.ProfileAccountDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Map account to profileAccountDto
 *
 * @author mctran
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mappings({@Mapping(source = "profile.imageLink",target = "imageLink")})
    ProfileAccountDto profileAndAcountToProfileAccountDto(Account account, Profile profile);

}