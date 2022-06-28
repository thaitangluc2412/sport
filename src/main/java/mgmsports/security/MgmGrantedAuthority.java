package mgmsports.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * Implementation of GrantedAuthority.
 * Simpler than Spring Security's SimpleGrantedAuthority
 * and easily use :).
 *
 * @author Chuc Ba Hieu
 */
public class MgmGrantedAuthority implements GrantedAuthority {

    private String authority;

    public MgmGrantedAuthority() {
    }

    public MgmGrantedAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
