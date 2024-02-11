package cl.previred.challenge.helper;

import cl.previred.challenge.config.JwtConfig;
import cl.previred.challenge.exceptions.AccessDeniedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;

public class JwtHelper {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(JwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    private static final int MINUTES = 60;

    private JwtHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateToken(String username) {
        var now = Instant.now();
        return Jwts.builder()
                .claim("sub", username)
                .claim("iat", Date.from(now))
                .expiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    public static boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private static Claims getTokenBody(String token) {
        try {
            return Jwts.parser() // Cambiado para usar parserBuilder()
                    .verifyWith(SECRET_KEY) // Actualizado según la recomendación
                    .build()
                    .parseSignedClaims(token) // También actualizado para usar parseClaimsJws
                    .getPayload();
        } catch (SignatureException | ExpiredJwtException e) { // Invalid signature or expired token
            throw new AccessDeniedException("Access denied: " + e.getMessage());
        }
    }

    private static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }
}
