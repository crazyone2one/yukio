package cn.master.yukio.security.filter;

import cn.master.yukio.entity.User;
import cn.master.yukio.security.CustomUserDetailsService;
import cn.master.yukio.security.JwtProvider;
import cn.master.yukio.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.master.yukio.entity.table.UserTableDef.USER;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;
    private final IUserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.auto-refresh-ttl}")
    private long autoRefreshTtl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        String username;
        boolean isTokenValid;
        try {
            isTokenValid = jwtProvider.isTokenValid(token);
        } catch (Exception e) {
            render(412, "无效凭证", response);
            return;
        }
        String requestUri = request.getRequestURI();
        if (isTokenValid) {
            Claims claims = jwtProvider.parseTokenToClaims(token);
            String tokenType = claims.getAudience();
            User user = userService.queryChain().where(USER.NAME.eq(claims.getSubject())).one();
            if (!Objects.equals("/api/auth/refresh", requestUri)) {
                switch (tokenType) {
                    case "refresh-token":
                        username = null;
                        log.warn("用户 '{}' 携带的凭证与请求类型不匹配", user.getName());
                        break;
                    case "access-token":
                        username = claims.getSubject();
                        if (!Objects.equals("/api/auth/logout", requestUri) && (claims.getExpiration().getTime() - new Date(System.currentTimeMillis()).getTime()) <= autoRefreshTtl) {
                            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                            String accessToken = jwtProvider.createAccessToken(userDetails);
                            response.setHeader("Authorization", "Bearer " + accessToken);
                            log.info("用户 '{}' 刷新了 access-token", user.getName());
                        }
                        break;
                    default:
                        username = null;
                        log.warn("用户 '{}' 携带的凭证的类型有误", user.getName());
                        break;
                }
            } else {
                switch (tokenType) {
                    case "access":
                        username = null;
                        log.warn("用户 '{}' 携带的凭证与请求类型不匹配", user.getName());
                        break;
                    case "refresh":
                        username = claims.getSubject();
                        break;
                    default: {
                        username = null;
                        log.warn("用户 '{}' 携带的凭证的类型有误", user.getName());
                        break;
                    }
                }
            }
        } else {
            if (Objects.equals("/api/auth/logout", requestUri)) {
                User user = userService.queryChain().where(USER.NAME.eq(jwtProvider.getExpiredTokenClaims(token).getSubject())).one();
                log.info("用户 '{}' 已退出登录", user.getName());
                render(200, "已退出登录", response);
                return;
            } else if (Objects.equals("/api/auth/refresh", requestUri)) {
                render(419, "请重新登录", response);
                return;
            } else {
                render(401, "访问凭证已过期", response);
                return;
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtProvider.isTokenValid(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request, response);
        }
    }

    private void render(int code, String msg, HttpServletResponse response) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", msg);
        response.setContentType("application/json;charset=UTF-8");
        String result = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map);
        try {
            response.getWriter().println(result);
        } finally {
            response.getWriter().close();
        }
    }
}
