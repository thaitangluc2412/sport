package mgmsports.common.social.model.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FacebookProfile {

    private String id;
    private String email;
    private String name;
    private Picture picture;
    private String birthday;
    @JsonProperty("favorite_athletes")
    private List<Experience> favoriteAthletes;
    @JsonProperty("favorite_teams")
    private List<Experience> favoriteTeams;

}
