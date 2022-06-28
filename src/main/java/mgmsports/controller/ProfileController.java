package mgmsports.controller;

import lombok.extern.slf4j.Slf4j;
import mgmsports.common.exception.EntityNotFoundException;
import mgmsports.common.exception.InvalidFileException;
import mgmsports.model.ProfileDto;
import mgmsports.model.Status;
import mgmsports.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * get profileDto by accountId
     *
     * @param accountId passed to get profile of this account
     * @return user profile information
     */
    @GetMapping({"/id/{accountId}"})
    public ResponseEntity<Object> getProfileByAccountId(@PathVariable("accountId") String accountId) {
        Optional<ProfileDto> profileDto = Optional.ofNullable(profileService.getProfileByAccountId(accountId));
        if (profileDto.isPresent()) {
            return new ResponseEntity<>(profileDto.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No profile exits", HttpStatus.OK);
    }

    /**
     * get all profiles by fullName for searching user by fullName
     *
     * @param fullName passed to find any user that fullName containing this param
     * @return list of user profiles
     */
    @GetMapping({"/{fullName}"})
    public ResponseEntity<Object> getAccountsByFullName(@PathVariable("fullName") String fullName) {
        Optional<List<ProfileDto>> profileDtos = Optional.ofNullable(profileService.getAllUsersByFullName(fullName));
        if (profileDtos.isPresent()) {
            return new ResponseEntity<>(profileDtos.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No profile exits", HttpStatus.OK);
    }

    /**
     * get all profiles by full name that do not contain input account
     *
     * @param fullName  passed to find user that fullName containing this param
     * @param accountId passed to not find this account
     * @return list of user profile
     */
    @GetMapping({"/{accountId}/{fullName}"})
    public ResponseEntity<Object> getProfilesByFullNameNotContainAccount(@PathVariable("fullName") String fullName, @PathVariable("accountId") String accountId) {
        Optional<List<ProfileDto>> profileDtos = Optional.ofNullable(profileService.getAllUsersByFullNameNotContainAccount(fullName, accountId));
        if (profileDtos.isPresent()) {
            return new ResponseEntity<>(profileDtos.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No profile exits", HttpStatus.OK);
    }

    /**
     * Get all status
     *
     * @return list of users status
     */
    @GetMapping("/status")
    public ResponseEntity<Object> getStatus() {
        Optional<Status[]> optionalStatus = Optional.ofNullable(profileService.getStatus());
        if (optionalStatus.isPresent()) {
            return new ResponseEntity<>(optionalStatus.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No status exits", HttpStatus.OK);
    }

    /**
     * Update a profile
     *
     * @param profileDto inputUpdateProfileDto
     * @param profileImage profile image
     * @return response
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws InvalidFileException InvalidFileException
     */
    @PutMapping
    public ResponseEntity<Object> updateProfile(@RequestPart("profile") @Valid ProfileDto profileDto, MultipartFile profileImage) throws EntityNotFoundException, InvalidFileException {
        profileService.updateProfile(profileDto, profileImage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resetImage/{accountId}")
    public ResponseEntity<Object> resetToDefaultImage(@PathVariable("accountId") String accountId) {
        String defaultImage = profileService.resetToDefaultImage(accountId);
        return  new ResponseEntity<>(defaultImage, HttpStatus.OK);
    }
}
