package mgmsports.service.impl;

import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.exception.InvalidFileException;
import mgmsports.common.mapper.ProfileMapper;
import mgmsports.dao.entity.Profile;
import mgmsports.dao.repository.ProfileRepository;
import mgmsports.model.ProfileDto;
import mgmsports.model.Status;
import mgmsports.service.FileStorageService;
import mgmsports.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private ProfileMapper profileMapper;
    private ProfileRepository profileRepository;
    private FileStorageService profileImageService;

    @Autowired
    public ProfileServiceImpl(ProfileMapper profileMapper, ProfileRepository profileRepository, @Qualifier("profileImageService") FileStorageService profileImageService) {
        this.profileMapper = profileMapper;
        this.profileRepository = profileRepository;
        this.profileImageService = profileImageService;
    }

    /**
     * Get profileDto by accountId
     *
     * @param accountId account_id
     * @return profileDto
     */
    @Override
    public ProfileDto getProfileByAccountId(String accountId) {
        return profileMapper.profileToProfileDto(profileRepository.findProfileByAccount_AccountId(accountId));
    }

    /**
     * Get list of profile by fullName
     *
     * @param fullName full_name
     * @return list of profileDto
     */
    @Override
    public List<ProfileDto> getAllUsersByFullName(String fullName) {
        List<Profile> profileList = profileRepository.findAllByFullNameContainingIgnoreCase(fullName.toLowerCase());
        return profileList.stream().map(profileMapper::profileToProfileDto).collect(Collectors.toList());
    }

    /**
     * Get one profile by accountId
     *
     * @param accountId account_id
     * @return one profileDto
     */
    @Override
    public ProfileDto getOneProfileByAccountId(String accountId) {
        return profileMapper.profileToProfileDto(profileRepository.findProfileByAccount_AccountId(accountId));
    }

    /**
     * Save a profile to database
     *
     * @param profile profile data
     */
    @Override
    public void saveProfile(Profile profile) {
        profileRepository.save(profile);
    }

    /**
     * Get list of profile by full name without account
     *
     * @param fullName fullName
     * @param accountId accountId
     * @return a list of profile dto
     */
    @Override
    public List<ProfileDto> getAllUsersByFullNameNotContainAccount(String fullName, String accountId) {
        List<Profile> profileList = profileRepository.findProfilesByAccount_AccountIdNotContainsAndFullNameContainsIgnoreCase(fullName.toLowerCase(), accountId);
        return profileList.stream().map(profileMapper::profileToProfileDto).collect(Collectors.toList());

    }

    /**
     * get list of user status
     *
     * @return list of users status
     */
    @Override
    public Status[] getStatus() {
        return Status.class.getEnumConstants();
    }

    /**
     * Update a profile
     *
     * @param profileDto input profile dto
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws InvalidFileException InvalidFileException
     *
     */
    @Override
    public void updateProfile(ProfileDto profileDto, MultipartFile profileImage) throws EntityNotFoundException, InvalidFileException {
        Profile profile = profileRepository.findProfileByAccount_AccountId(profileDto.getAccountId());
        if (profile == null) {
            throw new EntityNotFoundException(Profile.class, "AccountId", profileDto.getAccountId());
        }
        ProfileMapper.INSTANCE.updateProfileFromProfileDto(profileDto, profile);
        String fileUrl;
        if (profileImage != null) {
            fileUrl = handleImageFile(profile, profileImage);
            profile.setImageLink(fileUrl);
        }
        profileRepository.save(profile);
    }

    /**
     * Handle profile image
     *
     * @param profile Object profile
     * @param profileImage profile image file
     * @return uri
     * @throws InvalidFileException InvalidFileException
     */
    private String handleImageFile(Profile profile, MultipartFile profileImage) throws InvalidFileException {
        if (profileImage.getOriginalFilename() == null) {
            throw new InvalidFileException("File doesn't exist");
        }
        if (!profileImageService.isValidImageExtension(profileImage.getOriginalFilename().toLowerCase())) {
            throw new InvalidFileException("Must be a image file extension!");
        }
        String newName = profile.getAccount().getAccountId() + profile.getProfileId();
        String fileName = profileImageService.storeFile(profileImage, newName);
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/image/")
                .path(fileName)
                .toUriString();
    }

    @Override
    public String resetToDefaultImage(String accountId) {
        Profile profile = profileRepository.findProfileByAccount_AccountId(accountId);
        profile.setImageLink(profile.getBackupImage());
        return profile.getImageLink();
    }

}