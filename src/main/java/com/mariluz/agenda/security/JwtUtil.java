package com.mariluz.agenda.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

    // extrae el secret de 'application.properties'
    @Value("${jwt.secret}")
    private String jwtSecret;

    // extrae la expiracion (milisegundos) de 'application.properties'
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // crea una variable privada para almacenar la SecretKey
    private SecretKey key;

    // extrae la SecretKey y la almacena en 'key'
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // get username del token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // token validator
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (
            username.equals(userDetails.getUsername()) && !isTokenExpired(token)
        );
    }

    // get claims
    public Claims getAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // get claim (extrae un claim en especifico)
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // obtiene la fecha de expiracion
    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    // valida si el token esta expirado
    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    // ------ Funciones publicas para extraer claims del token (datos del usuario) ------
    // extraer id usuario
    public UUID getUserIdFromToken(String token) {
        return getClaim(token, claims -> claims.get("id", UUID.class));
    }

    // extraer Username
    public String getNameFromToken(String token) {
        return getClaim(token, claims -> claims.get("name", String.class));
    }

    // extraer email
    public String getEmailFromToken(String token) {
        return getClaim(token, claims -> claims.get("email", String.class));
    }

    // extraer rol
    public Integer getRoleFromToken(String token) {
        return getClaim(token, claims -> claims.get("role", Integer.class));
    }
}
