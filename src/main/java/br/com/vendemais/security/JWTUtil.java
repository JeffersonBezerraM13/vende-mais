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
 * Componente responsável pela geração de tokens JWT e fornecimento da chave secreta usada para assinatura.
 */
@Component
public class JWTUtil {

    /**
     * Tempo de expiração do token (em milissegundos), definido no application.properties.
     * Exemplo: 180000 (3 minutos)
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Chave secreta codificada em Base64, usada para assinar o token.
     * Deve ter no mínimo 512 bits para o algoritmo HS512.
     */
    @Value("${jwt.secret}")
    private String jwtSecretBase64;

    /**
     * Gera um token JWT com base no e-mail do usuário.
     *
     * @param email Identificador do usuário (geralmente o e-mail)
     * @return String contendo o token JWT gerado
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Define a informação principal (subject) do token: o e-mail do usuário
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Define a data de expiração
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) // Assina o token com a chave segura
                .compact(); // Compacta o token (gera a string final do JWT)
    }

    /**
     * Decodifica a chave secreta em Base64 e retorna uma instância de SecretKey compatível com HS512.
     *
     * @return SecretKey segura e válida para uso com o JWT
     */
    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Verifica se o token é válido (assinado corretamente e não expirado).
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
     * Extrai o nome de usuário (subject) do token.
     */
    public String getUserName(String token) {
        Claims claims = getClaims(token);
        if(claims != null){
            return claims.getSubject();
        }
        return null;
    }

    /**
     * Obtém os Claims (dados) contidos no token JWT (Base64 - Retornado no metodo .getSecretKey()).
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