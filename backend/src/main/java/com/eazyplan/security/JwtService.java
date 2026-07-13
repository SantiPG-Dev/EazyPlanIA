package com.eazyplan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * Generación y validación de tokens JWT (HS256).
 *
 * <p>El token incluye como {@code subject} el username y como claim personalizada
 * el {@code userId}, de modo que el backend puede identificar al usuario sin
 * consultar la base de datos en cada petición filtrada.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(
            @Value("${eazyplan.security.jwt.secret}") String secret,
            @Value("${eazyplan.security.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodeIfPlain(secret)));
        this.expirationMillis = expirationMinutes * 60_000;
    }

    /** Genera un JWT firmado para el usuario indicado. */
    public String generateToken(String username, Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    /** Extrae el username (subject) del token; lanza si está malformado o expirado. */
    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    /** Extrae el userId del claim personalizado. */
    public Long extractUserId(String token) {
        return parse(token).get("userId", Long.class);
    }

    /** Devuelve {@code true} si el token es válido (firma correcta y no expirado). */
    public boolean isTokenValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Acepta un secret en Base64 o texto plano (si no parece Base64 válido lo codifica en UTF-8).
     * Garantiza que la clave tenga al menos 256 bits para HS256.
     */
    private static String encodeIfPlain(String secret) {
        try {
            Decoders.BASE64.decode(secret);
            return secret; // ya está en Base64
        } catch (Exception e) {
            return java.util.Base64.getEncoder().encodeToString(secret.getBytes());
        }
    }
}
