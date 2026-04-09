package br.com.vendemais.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Provides JWT creation and validation utilities used by the CRM authentication
 * flow.
 */
@Component
public class JWTUtil {

    /**
     * Token expiration time in milliseconds, configured through application
     * properties.
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Base64-encoded secret key used to sign the token with the HS512 algorithm.
     */
    @Value("${jwt.secret}")
    private String jwtSecretBase64;

    /**
     * Generates a signed JWT for the authenticated user identified by email.
     *
     * @param email email used as the token subject
     * @return signed JWT that can be used to access protected endpoints
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Define a informaÃ§Ã£o principal (subject) do token: o e-mail do usuÃ¡rio
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Define a data de expiraÃ§Ã£o
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) // Assina o token com a chave segura
                .compact(); // Compacta o token (gera a string final do JWT)
    }

    /**
     * Decodes the configured secret and returns a signing key compatible with
     * HS512.
     *
     * @return secret key used to sign and validate JWT tokens
     */
    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Validates that the token is correctly signed and still within its validity
     * window.
     *
     * @param token JWT received from the client
     * @return {@code true} when the token is valid and not expired
     */
    public boolean validToken(String token) {
        Claims claims = getClaims(token);
        if(claims != null) {
            String userName = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());

            if(userName != null && expirationDate != null && now.before(expirationDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts the user identifier stored as the token subject.
     *
     * @param token JWT received from the client
     * @return email stored in the token subject, or {@code null} when the token is invalid
     */
    public String getUserName(String token) {
        Claims claims = getClaims(token);
        if(claims != null){
            return claims.getSubject();
        }
        return null;
    }

    /**
     * Parses the claims embedded in the JWT using the configured signing key.
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
