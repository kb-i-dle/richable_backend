package com.idle.kb_i_dle_backend.domain.member.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public final class JwtProcessor {
    static private final long TOKEN_VALID_MILISECOND = 1000L * 60 * 60 * 24; // 24h
    private static final String ENCRYPTION_SECRET = "TPk93NCNEQKs66+Ht89m+qVM8WkXoysjxanI7qh9hK0=";

    // For development use
    private String secretKey = "as13dfoipqieunxc_vm.hblih37y21.904-5jkd_glbovocipx98-58495ht9348hif0hg98dfugoih345n78t53yfihgkjcvhaa8sdr9wehurhlkcjxhz";
    private Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    // JWT generation
    public String generateToken(String subject, Integer uid, String nickname, String email) {
        String encryptedUid = AESUtil.encrypt(uid.toString(), ENCRYPTION_SECRET);

        return Jwts.builder()
                .setSubject(subject)
                .claim("uid", encryptedUid)
                .claim("nickname", nickname)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + TOKEN_VALID_MILISECOND))
                .signWith(key)
                .compact();
    }

    // Extract username (subject) from JWT
    public String getId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract uid from JWT
    public Integer getUid(String token) {
        String encryptedUid = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("uid", String.class);

        String decryptedUid = AESUtil.decrypt(encryptedUid, ENCRYPTION_SECRET);
        return Integer.parseInt(decryptedUid);
    }

    // Extract nickname from JWT
    public String getNickname(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("nickname", String.class);
    }

    // Validate JWT
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // Token is invalid
        }
    }
}