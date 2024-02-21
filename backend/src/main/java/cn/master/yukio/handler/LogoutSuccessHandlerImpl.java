package cn.master.yukio.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "已退出登录");
        response.setContentType("application/json;charset=UTF-8");
        String result = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map);
        try {
            response.getWriter().println(result);
        } finally {
            response.getWriter().close();
        }
    }
}
