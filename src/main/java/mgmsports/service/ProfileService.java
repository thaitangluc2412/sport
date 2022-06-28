package mgmsports.service;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.exception.InvalidFileException;
import mgmsports.dao.entity.Profile;
import mgmsports.model.ProfileDto;
import mgmsports.model.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    ProfileDto getProfileByAccountId(String accountId);

    List<ProfileDto> getAllUsersByFullName(String fullName);

    ProfileDto getOneProfileByAccountId(String accountId);
    void saveProfile(Profile profile);

    List<ProfileDto> getAllUsersByFullNameNotContainAccount(String fullName, String accountId);

    void updateProfile(ProfileDto profileDto, MultipartFile profileImage) throws EntityNotFoundException, InvalidFileException;

    Status[] getStatus();

    String  resetToDefaultImage(String accountId);
}