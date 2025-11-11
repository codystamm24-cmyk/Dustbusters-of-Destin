package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import model.User;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
    // Read JWT secret from environment (base64 encoded). If missing, generate a temporary key (dev only).
    private static final Key key;
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    static {
        String secretBase64 = System.getenv("JWT_SECRET");
        if (secretBase64 != null && !secretBase64.isBlank()) {
            try {
                byte[] secretBytes = Base64.getDecoder().decode(secretBase64);
                key = Keys.hmacShaKeyFor(secretBytes);
            } catch (IllegalArgumentException e) {
                // if decoding fails, fall back to generated key
                System.err.println("Invalid JWT_SECRET (not valid base64), falling back to generated key for dev.");
                key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
        } else {
            System.err.println("JWT_SECRET not set — generating ephemeral key (dev only). Set JWT_SECRET for production.");
            key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    public static String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static Claims verifyToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}