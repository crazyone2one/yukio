package cn.master.yukio.handler;

import cn.master.yukio.exception.IResultCode;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.handler.result.MsHttpResultCode;
import cn.master.yukio.util.ServiceUtils;
import cn.master.yukio.util.Translator;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by 11's papa on 02/22/2024
 **/
@RestControllerAdvice
public class RestControllerExceptionHandler {
    /**
     * 处理数据校验异常，返回具体字段的校验信息
     * http 状态码返回 400
     *
     * @param ex MethodArgumentNotValidException
     * @return cn.master.yukio.handler.ResultHolder
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultHolder handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResultHolder.error(MsHttpResultCode.VALIDATE_FAILED.getCode(), MsHttpResultCode.VALIDATE_FAILED.getMessage(), errors);
    }

    /**
     * http 状态码返回405
     *
     * @param response  HttpServletResponse
     * @param exception Exception
     * @return cn.master.yukio.handler.ResultHolder
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultHolder handleHttpRequestMethodNotSupportedException(HttpServletResponse response, Exception exception) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        return ResultHolder.error(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage());
    }

    /**
     * 根据 MSException 中的 errorCode，设置对应的 Http 状态码，以及业务状态码和错误提示
     *
     * @param e MSException
     * @return org.springframework.http.ResponseEntity<cn.master.yukio.handler.ResultHolder>
     */
    @ExceptionHandler(MSException.class)
    public ResponseEntity<ResultHolder> handlerMsException(MSException e) {
        IResultCode errorCode = e.getErrorCode();
        if (errorCode == null) {
            // 如果抛出异常没有设置状态码，则返回错误 message
            return ResponseEntity.internalServerError()
                    .body(ResultHolder.error(MsHttpResultCode.FAILED.getCode(), e.getMessage()));
        }

        int code = errorCode.getCode();
        String message = errorCode.getMessage();
        message = Translator.get(message, message);

        if (errorCode instanceof MsHttpResultCode) {
            // 如果是 MsHttpResultCode，则设置响应的状态码，取状态码的后三位
            if (errorCode.equals(MsHttpResultCode.NOT_FOUND)) {
                message = getNotFoundMessage(message);
            }
            return ResponseEntity.status(code % 1000)
                    .body(ResultHolder.error(code, message, e.getMessage()));
        } else {
            // 响应码返回 500，设置业务状态码
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultHolder.error(code, Translator.get(message, message), e.getMessage()));
        }
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResultHolder> handlerException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(ResultHolder.error(MsHttpResultCode.FAILED.getCode(),
                        e.getMessage(), getStackTraceAsString(e)));
    }

    public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    private static String getNotFoundMessage(String message) {
        String resourceName = ServiceUtils.getResourceName();
        if (StringUtils.isNotBlank(resourceName)) {
            message = String.format(message, Translator.get(resourceName, resourceName));
        } else {
            message = String.format(message, Translator.get("resource.name"));
        }
        ServiceUtils.clearResourceName();
        return message;
    }

}
