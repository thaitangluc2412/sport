package mgmsports.service.impl;

import mgmsports.dao.entity.Account;
import mgmsports.dao.entity.Profile;
import mgmsports.dao.repository.AccountRepository;
import mgmsports.dao.repository.ProfileRepository;
import mgmsports.model.FacebookInformationDto;
import mgmsports.service.FacebookLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Implement interface FacebookLoginService
 *
 * @author lamri
 */
@Service
public class FacebookLoginServiceImpl implements FacebookLoginService {

    private AccountRepository accountRepository;

    private ProfileRepository profileRepository;

    @Autowired
    public FacebookLoginServiceImpl(AccountRepository accountRepository, ProfileRepository profileRepository) {
        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Check if Facebook account already registered
     * YES: do nothing
     * NO: create new account
     *
     * @param facebookInformationDto facebook user data
     */
    @Override
    public void saveToDBIfUserNotRegistered(FacebookInformationDto facebookInformationDto) {
        if (!accountRepository.existsAccountBySocialId(facebookInformationDto.getFbUserId())) {
            try {
                saveUser(facebookInformationDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get accountId by fbUserId
     *
     * @param fbUserId facebook user ID
     * @return accountId
     */
    @Override
    public String getAccountIdByFbUserId(String fbUserId) {
        return accountRepository.getAccountIdBySocialId(fbUserId);
    }

    private void saveUser(FacebookInformationDto facebookInformationDto) throws Exception {
        Account newAccount = new Account();
        newAccount.setSocialId(facebookInformationDto.getFbUserId());
        newAccount.setEmail(facebookInformationDto.getEmail());
        Date date = new Date();

        newAccount.setCreatedDate(date);
        newAccount.setLastModifiedDate(date);

        Profile newProfile = new Profile();
        newProfile.setAccount(newAccount);
        newProfile.setImageLink(facebookInformationDto.getImageLink());
        newProfile.setFullName(facebookInformationDto.getFbUserName());

        accountRepository.save(newAccount);
        profileRepository.save(newProfile);
    }
}
