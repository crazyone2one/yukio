package cn.master.yukio.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Created by 11's papa on 03/01/2024
 **/
@Component
public class CommonBeanFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    public static Object getBean(String beanName) {
        try {
            return context != null && !StringUtils.isBlank(beanName) ? context.getBean(beanName) : null;
        } catch (BeansException e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> className) {
        try {
            return context != null && className != null ? context.getBean(className) : null;
        } catch (BeansException e) {
            return null;
        }
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> className) {
        return context.getBeansOfType(className);
    }
}
