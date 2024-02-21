package cn.master.yukio.handler;

import cn.master.yukio.security.CustomUserDetails;
import cn.master.yukio.util.RedisKeyUtils;
import cn.master.yukio.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutHandler extends SecurityContextLogoutHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomUserDetails currentUser = SessionUtils.getCurrentUser();
        String loggedUserKey = RedisKeyUtils.getLoggedUserKey(currentUser.getUser().getId());
        redisTemplate.delete(loggedUserKey);
        SecurityContextHolder.clearContext();
        log.info("用户 '{}' 已退出登录", currentUser.getUsername());
    }
}
