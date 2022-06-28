package mgmsports;

import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jwt.JWTClaimsSet;
import mgmsports.security.JwtService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

public class JwtServiceTests {

    // An aes-128-cbc key generated at https://asecuritysite.com/encryption/keygen (take the "key" field)
    private static final String SECRET1 = "926D96C90030DD58429D2751AC1BDBBC";
    private static final String SECRET2 = "538518AB685B514685DA8055C03DDA63";

    private JwtService service1;
    private JwtService service2;

    public JwtServiceTests() throws KeyLengthException {

        service1 = new JwtService(SECRET1);
        service2 = new JwtService(SECRET2);
    }

    public static <K,V> Map<K,V> mapOf(Object... keyValPair) {

        if(keyValPair.length % 2 != 0)
            throw new IllegalArgumentException("Keys and values must be in pairs");

        Map<K,V> map = new HashMap<K,V>(keyValPair.length / 2);

        for(int i = 0; i < keyValPair.length; i += 2){
            map.put((K) keyValPair[i], (V) keyValPair[i+1]);
        }

        return map;
    }

    @Test
    public void testJwtParseToken() {
        String token = service1.createToken("auth", "subject", 120000L, mapOf("accountId", "12345678"));
        JWTClaimsSet claims = service1.parseToken(token, "auth");

        Assert.assertEquals("subject", claims.getSubject());
        Assert.assertEquals("12345678", claims.getClaim("accountId"));
    }

    @Test(expected = AccessDeniedException.class)
    public void testJwtParseTokenWrongAudience() {

        String token = service1.createToken("auth", "subject", 5000L);
        service1.parseToken(token, "auth2");
    }

    @Test(expected = AccessDeniedException.class)
    public void testJwtParseTokenExpired() throws InterruptedException {

        String token = service1.createToken("auth", "subject", 1L);
        Thread.sleep(1L);
        service1.parseToken(token, "auth");
    }

    @Test(expected = BadCredentialsException.class)
    public void testJwtParseTokenWrongSecret() {

        String token = service1.createToken("auth", "subject", 5000L);
        service2.parseToken(token, "auth");
    }

    @Test(expected = AccessDeniedException.class)
    public void testParseTokenCutoffTime() throws InterruptedException {

        String token = service1.createToken("auth", "subject", 5000L);
        Thread.sleep(1L);
        service1.parseToken(token, "auth", System.currentTimeMillis());
    }
}

