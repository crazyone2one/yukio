package cn.master.yukio.util;

import cn.master.yukio.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Slf4j
public class SessionUtils {
    private static final ThreadLocal<String> PROJECT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ORGANIZATION_ID = new ThreadLocal<>();

    public static CustomUserDetails getCurrentUser() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getUserId() {
        CustomUserDetails currentUser = getCurrentUser();
        return Objects.isNull(currentUser) ? null : currentUser.getUser().getId();
    }

    public static void setCurrentOrganizationId(String organizationId) {
        SessionUtils.ORGANIZATION_ID.set(organizationId);
    }

    public static String getCurrentOrganizationId() {
        if (StringUtils.isNotEmpty(ORGANIZATION_ID.get())) {
            return ORGANIZATION_ID.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            log.debug("ORGANIZATION: {}", request.getHeader("ORGANIZATION"));
            if (request.getHeader("ORGANIZATION") != null) {
                return request.getHeader("ORGANIZATION");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return getCurrentUser().getUser().getLastOrganizationId();
    }

    public static void setCurrentProjectId(String projectId) {
        SessionUtils.PROJECT_ID.set(projectId);
    }

    public static String getCurrentProjectId() {
        if (StringUtils.isNotEmpty(PROJECT_ID.get())) {
            return PROJECT_ID.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            log.debug("PROJECT: {}", request.getHeader("PROJECT"));
            if (request.getHeader("PROJECT") != null) {
                return request.getHeader("PROJECT");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return getCurrentUser().getUser().getLastProjectId();
    }

    public static void clearCurrentOrganizationId() {
        ORGANIZATION_ID.remove();
    }

    public static void clearCurrentProjectId() {
        PROJECT_ID.remove();
    }
}
