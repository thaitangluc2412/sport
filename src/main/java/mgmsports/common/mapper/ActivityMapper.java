package mgmsports.common.mapper;

import mgmsports.dao.entity.Activity;
import mgmsports.model.ActivityDto;
import mgmsports.model.ActivityInputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;

/**
 * Map activity to activityDto
 *
 * @author qngo
 */
@Mapper(componentModel = "spring")
public interface ActivityMapper {

    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);

    @Mapping(source = "account.accountId", target = "accountId")
    ActivityDto activityToActivityDto(Activity Activity);

    @Mapping(target = "activityId", ignore = true)
    void updateActivityFromActivityInputDto(ActivityInputDto activityInputDto, @MappingTarget Activity activity) throws ParseException;

}
