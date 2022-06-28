package mgmsports.service;

import mgmsports.model.FacebookInformationDto;

public interface FacebookLoginService {
    void saveToDBIfUserNotRegistered(FacebookInformationDto facebookInformationDTO);

    String getAccountIdByFbUserId(String fbUserId);

}
