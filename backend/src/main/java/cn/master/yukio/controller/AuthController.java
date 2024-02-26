package cn.master.yukio.controller;

import cn.master.yukio.dto.LoginRequest;
import cn.master.yukio.handler.ResultHolder;
import cn.master.yukio.security.CustomUserDetails;
import cn.master.yukio.security.CustomUserDetailsService;
import cn.master.yukio.security.JwtProvider;
import cn.master.yukio.util.RedisKeyUtils;
import cn.master.yukio.util.SessionUtils;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailsService customUserDetailsService;
    @Value("${jwt.auto-refresh-ttl}")
    private long autoRefreshTtl;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername().trim(), user.getPassword().trim());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtProvider.createAccessToken(customUserDetails);
        String refreshToken = jwtProvider.createRefreshToken(customUserDetails);
        redisTemplate.opsForValue().set(RedisKeyUtils.getLoggedUserKey(customUserDetails.getUser().getId()), customUserDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, Object> map = new HashMap<>();
        map.put("token", accessToken);
        map.put("refresh_token", refreshToken);
        map.put("user", customUserDetails.getUser());

        log.info("{} logged in", customUserDetails.getUser().getName());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@NotBlank(message = "refreshToken不能为空") @RequestParam String refreshToken) {
        if (jwtProvider.isTokenValid(refreshToken)) {
            Map<String, Object> map = new HashMap<>();
            Claims claims = jwtProvider.parseTokenToClaims(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());
            if ((claims.getExpiration().getTime() - new Date(System.currentTimeMillis()).getTime()) <= autoRefreshTtl) {
                String newRefreshToken = jwtProvider.createRefreshToken(userDetails);
                map.put("refresh_token", newRefreshToken);
                log.info("{}'s refreshToken recreated.", claims.getSubject());
            }
            String accessToken = jwtProvider.createAccessToken(userDetails);
            map.put("token", accessToken);
            log.info("{}'s token refreshed.", claims.getSubject());
            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("请重新登录");
        }
    }

    @GetMapping(value = "/signout")
    public ResultHolder logout() {
        if (SessionUtils.getCurrentUser() == null) {
            return ResultHolder.success("logout success");
        }
        SecurityContextHolder.clearContext();
        return ResultHolder.success("logout success");
    }

    @GetMapping("/demo")
    public ResponseEntity<Object> demo() {
        return ResponseEntity.ok("demo");
    }
}
