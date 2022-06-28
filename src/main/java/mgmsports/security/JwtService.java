package mgmsports.security;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import mgmsports.common.MgmSportsUtils;
import mgmsports.common.property.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Service
 *
 * References:
 * https://connect2id.com/products/nimbus-jose-jwt/examples/jwe-with-shared-key
 * https://connect2id.com/products/nimbus-jose-jwt/examples/validating-jwt-access-tokens
 *
 * @author Chuc Ba Hieu
 */
public class JwtService {

    public static final String MGM_IAT = "mgm-iat";
    public static final String AUTH_AUDIENCE = "auth";

    private DirectEncrypter encrypter;
    private JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);
    private ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor;

    @Autowired
    private SecurityProperties properties;

    public JwtService(String secret) throws KeyLengthException {

        byte[] secretKey = secret.getBytes();
        encrypter = new DirectEncrypter(secretKey);
        jwtProcessor = new DefaultJWTProcessor<>();

        // The JWE key source
        JWKSource<SimpleSecurityContext> jweKeySource = new ImmutableSecret<>(secretKey);

        // Configure a key selector to handle the decryption phase
        JWEKeySelector<SimpleSecurityContext> jweKeySelector =
                new JWEDecryptionKeySelector<>(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256, jweKeySource);

        jwtProcessor.setJWEKeySelector(jweKeySelector);
    }

    /**
     * Creates a token
     */
    public String createToken(String aud, String subject, Long expirationMillis, Map<String, Object> claimMap) {

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        builder
                //.issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expirationMillis))
                .audience(aud)
                .subject(subject)
                .claim(MGM_IAT, System.currentTimeMillis());

        //claimMap.put("iat", new Date());
        claimMap.forEach(builder::claim);

        JWTClaimsSet claims = builder.build();

        Payload payload = new Payload(claims.toJSONObject());

        // Create the JWE object and encrypt it
        JWEObject jweObject = new JWEObject(header, payload);

        try {

            jweObject.encrypt(encrypter);

        } catch (JOSEException e) {

            throw new RuntimeException(e);
        }

        // Serialize to compact JOSE form...
        return jweObject.serialize();
    }

    /**
     * Creates a token
     */
    public String createToken(String audience, String subject, Long expirationMillis) {

        return createToken(audience, subject, expirationMillis, new HashMap<>());
    }

    /**
     * Parses a token
     */
    public JWTClaimsSet parseToken(String token, String audience) {

        try {

            JWTClaimsSet claims = jwtProcessor.process(token, null);
            MgmSportsUtils.ensureAuthority(audience != null &&
                            claims.getAudience().contains(audience),
                    "Wrong audience");

            MgmSportsUtils.ensureAuthority(claims.getExpirationTime().after(new Date()),
                    "Spring expiredToken");

            return claims;

        } catch (ParseException | BadJOSEException | JOSEException e) {

            throw new BadCredentialsException(e.getMessage());
        }
    }

    /**
     * Parses a token
     */
    public JWTClaimsSet parseToken(String token, String audience, long issuedAfter) {

        JWTClaimsSet claims = parseToken(token, audience);

        long issueTime = (long) claims.getClaim(MGM_IAT);
        MgmSportsUtils.ensureAuthority(issueTime >= issuedAfter,
                "Obsolete Token");

        return claims;
    }

    /**
     * Adds a Mgm-Authorization header to the response
     */
    public void addAuthHeader(HttpServletResponse response, String userId, Long expirationMillis) {
        response.addHeader(properties.getJwt().getTokenResponseHeaderName(),
                properties.getJwt().getTokenPrefix() + " " +
                        createToken(AUTH_AUDIENCE, userId, expirationMillis));
    }
}

