package ma.m3achaba.plantes.services.imp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import ma.m3achaba.plantes.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {

    private final SecretKey key;
    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 3600; // 1 heure
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 604800; // 7 jours

    public JwtUtils() {
        String secretString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = Base64.getEncoder().encode(secretString.getBytes()); // Correction : Encodage en Base64
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateAccessToken(UserDetails userDetails) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusSeconds(ACCESS_TOKEN_EXPIRATION_SECONDS);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", ((User) userDetails).getRole().name())
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(expiration))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(expiration))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String tokenUsername = extractUsername(token);
        final String tokenRole = extractRole(token);
        final String userRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        return (tokenUsername.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && tokenRole != null
                && tokenRole.equals(userRole));
    }

    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
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
                claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
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
