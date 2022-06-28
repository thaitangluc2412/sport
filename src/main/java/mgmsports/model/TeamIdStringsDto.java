package mgmsports.model;

import lombok.Data;

@Data
public class TeamIdStringsDto {
    private String teamId;

    public TeamIdStringsDto(String teamId) {
        this.teamId = teamId;
    }
}
