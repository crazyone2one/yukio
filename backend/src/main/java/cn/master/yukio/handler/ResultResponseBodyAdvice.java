package cn.master.yukio.handler;

import cn.master.yukio.handler.annotation.NoResultHolder;
import cn.master.yukio.util.JsonUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 统一处理返回结果集
 *
 * @author Created by 11's papa on 02/22/2024
 **/
@RestControllerAdvice
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType) || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 处理空值
        if (Objects.isNull(body) && StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonUtils.toJsonString(ResultHolder.success(body));
        }
        if (returnType.hasMethodAnnotation(NoResultHolder.class)) {
            return body;
        }
        if (!(body instanceof ResultHolder)) {
            if (body instanceof String) {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return JsonUtils.toJsonString(ResultHolder.success(body));
            }
            return ResultHolder.success(body);
        }
        return body;
    }
}
