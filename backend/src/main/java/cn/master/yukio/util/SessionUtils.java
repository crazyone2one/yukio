package cn.master.yukio.util;

import cn.master.yukio.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
public class SessionUtils {
    private static final ThreadLocal<String> PROJECT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> WORKSPACE_ID = new ThreadLocal<>();

    public static CustomUserDetails getCurrentUser() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
