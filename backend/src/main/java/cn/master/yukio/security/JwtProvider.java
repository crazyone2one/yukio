package cn.master.yukio.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${jwt.secret}")
    private String signature;

    @Value("${jwt.access-token-ttl}")
    private long accessTokenTtl;

    @Value("${jwt.refresh-token-ttl}")
    private long refreshTokenTtl;

    public String createAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, accessTokenTtl, "access-token");
    }

    public String createRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenTtl, "refresh-token");
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            switch (e.getClaims().getAudience()) {
                case "access-token":
                    log.info("用户 '{}' 的 accessToken 已过期", e.getClaims().getSubject());
                    break;
                case "refresh-token": {
                    log.info("用户 '{}' 的 refreshToken 已过期，已退出登录", e.getClaims().getSubject());
                }
            }
            return false;
        } catch (Exception e) {
            log.error("解析凭证出错: {}", e.getMessage());
            throw new JwtException(e.getMessage());
        }
    }

    public Claims parseTokenToClaims(String token) {
        if (isTokenValid(token)) {
            return Jwts.parserBuilder().setSigningKey(secretKey()).build().parseClaimsJws(token).getBody();
        }
        return null;
    }

    public Claims getExpiredTokenClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey()).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            log.error("错误的凭证: {}", e.getMessage());
            throw new JwtException(e.getMessage());
        }
    }

    private String generateToken(UserDetails userDetails, long expiration, String audience) {
        Date date = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setIssuer("11's papa")
                .setSubject(userDetails.getUsername())
                .setAudience(audience)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + expiration))
                .signWith(secretKey(), SignatureAlgorithm.HS256)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(signature.getBytes(StandardCharsets.UTF_8));
    }
}
