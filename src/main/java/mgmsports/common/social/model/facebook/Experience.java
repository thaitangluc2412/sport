package mgmsports.common.social.model.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Experience {
    private String id;
    @JsonProperty("name")
    private String description;
}
