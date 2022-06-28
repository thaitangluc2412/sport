package mgmsports.common.mapper;

import mgmsports.dao.entity.Profile;
import mgmsports.dao.entity.Team;

import mgmsports.model.TeamHeaderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Map team header to teamHeaderDto
 *
 * @author vtlu
 */
@Mapper(componentModel = "spring")
public interface TeamHeaderMapper {

    @Mappings({
            @Mapping(source = "profile.imageLink",target = "imageLink"),
            @Mapping(source = "profile.fullName",target = "fullName"),
            @Mapping(source = "team.name",target = "name"),
            @Mapping(source = "team.hostId",target = "hostId")
    })
    TeamHeaderDto teamAndProfileDtoToTeamHeaderDto(Team team, Profile profile);
}
