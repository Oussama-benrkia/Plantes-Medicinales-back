package ma.m3achaba.plantes.services.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import ma.m3achaba.plantes.model.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtUtils {

    private final SecretKey key;
    private static final long EXPIRATION_TIME = 86400; // 24 hours in seconds

    public JwtUtils() {
        String secretString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
    public String generateToken(User userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(toDate(LocalDateTime.now()))
                .setExpiration(toDate(LocalDateTime.now().plusSeconds(EXPIRATION_TIME)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, User userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(toDate(LocalDateTime.now()))
                .setExpiration(toDate(LocalDateTime.now().plusSeconds(EXPIRATION_TIME)))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        LocalDateTime expiration = extractExpiration(token);
        return expiration.isBefore(LocalDateTime.now());
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private LocalDateTime extractExpiration(String token) {
        return extractClaims(token, claims ->
                LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault())
        );
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
