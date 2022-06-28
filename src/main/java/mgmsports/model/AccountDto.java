package mgmsports.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * A lighter User class,
 * mainly used for holding logged-in user data
 */
@Data
public class AccountDto {

    private String id;
    private String username;
    private String email;
    private String password;
    private Set<String> roles = new HashSet<>();

    private boolean goodUser = false;

}
