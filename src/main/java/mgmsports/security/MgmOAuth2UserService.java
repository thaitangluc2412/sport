package mgmsports.security;

import lombok.extern.slf4j.Slf4j;
import mgmsports.common.social.Facebook;
import mgmsports.common.social.model.facebook.FacebookProfile;
import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Profile;
import mgmsports.model.AccountDto;
import mgmsports.service.AccountService;
import mgmsports.service.ProfileService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * Logs in or registers a user after OAuth2 SignIn/Up
 *
 * @author Chuc Ba Hieu
 */
@Slf4j
public class MgmOAuth2UserService extends DefaultOAuth2UserService {

    private AccountService accountService;
    private ProfileService profileService;

    public MgmOAuth2UserService(AccountService accountService, ProfileService profileService) {
        this.accountService = accountService;
        this.profileService = profileService;
        log.info("Created");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oath2User = super.loadUser(userRequest);
        return buildPrincipal(oath2User, userRequest.getClientRegistration().getRegistrationId(), userRequest.getAccessToken());
    }

    /**
     * Builds the security principal from the given userReqest.
     * Registers the user if not already reqistered
     */
    public MgmSportsPrincipal buildPrincipal(OAuth2User oath2User, String registrationId, OAuth2AccessToken accessToken) {

        Map<String, Object> attributes = oath2User.getAttributes();
        String socialId = oath2User.getName();
        Account user = checkUser(socialId, registrationId, accessToken.getTokenValue(), attributes);

        AccountDto userDto = user.toAccountDto();
        MgmSportsPrincipal principal = new MgmSportsPrincipal(userDto);
        principal.setAttributes(attributes);
        principal.setName(oath2User.getName());

        return principal;
    }

    private Account checkUser (String socialId, String registrationId, String accessToken, Map<String, Object> attributes) {

        return accountService.findUserBySocialId(socialId).orElseGet(() -> {

            switch (registrationId) {

                case "facebook":
                    Facebook facebook = new Facebook(accessToken);
                    FacebookProfile facebookProfile =  facebook.getProfile();
                    return saveNewFacebookAccount(facebookProfile);

                case "google":
                    return saveNewGoogleAccount(attributes);

                default:
                    throw new UnsupportedOperationException("Fetching name from " + registrationId + " login not supported");
            }

        });
    }

    private Account saveNewFacebookAccount (FacebookProfile facebookProfile) {
        Account newUser = new Account();
        Profile newProfile = new Profile();
        newProfile.setAccount(newUser);
        newProfile.setImageLink(facebookProfile.getPicture().getData().getUrl());
        newProfile.setBackupImage(facebookProfile.getPicture().getData().getUrl());
        newProfile.setFullName(facebookProfile.getName());
        newUser.setEmail(facebookProfile.getEmail());
        newUser.setSocialId(facebookProfile.getId());
        newUser.setUserName(facebookProfile.getId());
        accountService.saveUser(newUser);
        profileService.saveProfile(newProfile);
        return newUser;
    }

    private Account saveNewGoogleAccount (Map<String, Object> attributes) {
        Account newUser = new Account();
        Profile newProfile = new Profile();
        newProfile.setAccount(newUser);
        newProfile.setImageLink((String) attributes.get(StandardClaimNames.PICTURE));
        newProfile.setBackupImage((String) attributes.get(StandardClaimNames.PICTURE));
        newProfile.setFullName((String) attributes.get(StandardClaimNames.NAME));
        newUser.setEmail((String) attributes.get(StandardClaimNames.EMAIL));
        newUser.setSocialId((String) attributes.get(StandardClaimNames.SUB));
        newUser.setUserName((String) attributes.get(StandardClaimNames.SUB));
        accountService.saveUser(newUser);
        profileService.saveProfile(newProfile);
        return newUser;
    }

}
