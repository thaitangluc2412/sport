package mgmsports.common.mapper;

import mgmsports.dao.entity.Profile;
import mgmsports.model.ProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    @Mapping(source = "account.accountId", target = "accountId")
    ProfileDto profileToProfileDto(Profile profile);

    Profile profileDtoToProfile(ProfileDto profileDto);

    @Mapping(target = "account.accountId", ignore = true)
    void updateProfileFromProfileDto(ProfileDto profileDto, @MappingTarget Profile profile);
}