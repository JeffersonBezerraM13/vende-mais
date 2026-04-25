package br.com.vendemais.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

/**
 * Provides JWT creation and validation utilities used by the CRM authentication
 * flow.
 */
@Component
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.expiration}")
    private Long expiration;

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
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
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
     * Validates that the token is correctly signed, contains a subject, and is
     * still within its validity window.
     *
     * @param token JWT received from the client
     * @return {@code true} when the token is valid and not expired
     */
    public boolean validToken(String token) {
        return getClaims(token)
                .map(claims -> {
                    String username = claims.getSubject();
                    Date expirationDate = claims.getExpiration();
                    Date now = new Date(System.currentTimeMillis());

                    return username != null
                            && expirationDate != null
                            && now.before(expirationDate);
                })
                .orElse(false);
    }

    /**
     * Extracts the user identifier stored as the token subject.
     *
     * @param token JWT received from the client
     * @return email stored in the token subject, or {@code null} when the token is invalid
     */
    public String getUserName(String token) {
        return getClaims(token)
                .map(Claims::getSubject)
                .orElse(null);
    }

    /**
     * Parses the claims embedded in the JWT using the configured signing key.
     * Invalid tokens are rejected without exposing sensitive token contents in
     * application logs.
     *
     * @param token JWT received from the client
     * @return parsed claims when the token is valid
     */
    private Optional<Claims> getClaims(String token) {
        if (token == null || token.isBlank()) {
            logger.debug("JWT token ausente ou vazio.");
            return Optional.empty();
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.of(claims);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT expirado: {}", e.getMessage());
            return Optional.empty();
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("JWT inválido: {}", e.getClass().getSimpleName());
            logger.debug("Detalhes da falha ao validar JWT.", e);
            return Optional.empty();
        }
    }
}