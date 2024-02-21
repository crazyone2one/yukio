package cn.master.yukio.controller;

import cn.master.yukio.dto.LoginRequest;
import cn.master.yukio.entity.User;
import cn.master.yukio.security.CustomUserDetails;
import cn.master.yukio.security.JwtProvider;
import cn.master.yukio.util.RedisKeyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        map.put("refreshToken", refreshToken);

        log.info("{} logged in", customUserDetails.getUser().getName());
        return ResponseEntity.ok(map);
    }
}
